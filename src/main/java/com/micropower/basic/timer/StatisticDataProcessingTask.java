package com.micropower.basic.timer;

import com.micropower.basic.entity.FlowRecordBean;
import com.micropower.basic.entity.StationBean;
import com.micropower.basic.netty.WaitingQueueService;
import com.micropower.basic.service.CompanyService;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.service.RealtimeService;
import com.micropower.basic.service.StationService;
import com.micropower.basic.util.DateUtil;
import com.micropower.basic.util.RedisUtil;
import com.micropower.basic.util.StaticFinalWard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/4/29 14:31
 */
@Component
@Slf4j
public class StatisticDataProcessingTask {

    private static final String ZERO = "0.000";
    private static final String LEVEL = "levelHeight";
    private static final String SPEED = "speed";
    private static final String FLOW = "flow";

    private static StatisticDataProcessingTask task;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RealtimeService realtimeService;
    @Autowired
    private OperationRecordService operationRecordService;
    @Autowired
    private StationService stationService;

    @PostConstruct
    public void init() {
        task = this;
        task.redisUtil = this.redisUtil;
        task.realtimeService = this.realtimeService;
        task.stationService = this.stationService;
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加流量日统计（即小时记录）数据
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "00 52 * * * ?")
    private void insertHourRecord() {
        String time = DateUtil.beforeHourStrToNow(1);
        List<StationBean> list = task.stationService.getFlowStationList();
        Map<String, Object> param = DateUtil.getOneHourAgoRange();
        //保存数据List
        for (StationBean station : list) {
            int stationId = station.getId();
            param.put("stationId", stationId);
            try {
                //获取最新的累积流量值
                Map<String, Object> maxFlowMap = task.operationRecordService.getMaxFlow(param);
                if (maxFlowMap != null) {
                    String maxFlow = maxFlowMap.get("total_flow").toString();
                    //获取上一小时的累积流量值
                    Object totalFlow = task.redisUtil.hget(StaticFinalWard.STATION + station.getId(), StaticFinalWard.HOUR_FLOW);
                    String startFlowStr = totalFlow == null || totalFlow == "" ? "0.0" : totalFlow.toString();
                    Double endFlow = "".equals(maxFlow.trim()) ? 0.00 : Double.valueOf(maxFlow);
                    Double startFlow = "".equals(startFlowStr.trim()) ? 0.00 : Double.valueOf(startFlowStr);
                    FlowRecordBean record = new FlowRecordBean();
                    record.setTime(time);
                    record.setStationId(stationId);
                    record.setTotalFlow(String.valueOf(endFlow));
                    double emissions = endFlow - startFlow;
                    emissions = emissions < 0 ? 0 : emissions;
                    record.setEmissions(String.format("%.2f", emissions));
                    record.setRange(String.valueOf(param.get("range")));
                    record.setLevelHeight(ZERO);
                    record.setSpeed(ZERO);
                    record.setFlow(ZERO);
                    Map<String, Object> avg = task.operationRecordService.getFlowAvg(param);
                    if (avg != null) {
                        if (avg.get(LEVEL) != null) {
                            record.setLevelHeight(String.valueOf(avg.get(LEVEL)));
                        }
                        if (avg.get(SPEED) != null) {
                            record.setSpeed(String.valueOf(avg.get(SPEED)));
                        }
                        if (avg.get(FLOW) != null) {
                            record.setFlow(String.valueOf(avg.get(FLOW)));
                        }
                    }
                    task.redisUtil.hset(StaticFinalWard.STATION + stationId, StaticFinalWard.HOUR_FLOW, maxFlow);
                    task.realtimeService.insertFlowHourRecord(record);
                }
            } catch (Exception e) {
                log.error("流量小时记录异常[" + station.getName() + "，ID：" + station.getId() + "]", e);
            }
        }
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加流量月统计（即日记录）
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "00 30 01 * * ?")
    private void insertDayRecord() {
        String time = DateUtil.beforeDayStrToNow(1);
        List<StationBean> list = task.stationService.getFlowStationList();
        Map<String, Object> param = DateUtil.getOneDayAgoRange();
        for (StationBean station : list) {
            int stationId = station.getId();
            param.put("stationId", stationId);
            try {
                Map<String, Object> maxFlowMap = task.operationRecordService.getMaxFlow(param);
                if (maxFlowMap != null) {
                    String maxFlow = maxFlowMap.get("total_flow").toString();
                    Object totalFlow = task.redisUtil.hget(StaticFinalWard.STATION + stationId, StaticFinalWard.DAY_FLOW);
                    String startFlowStr = totalFlow == null || totalFlow == "" ? "0.0" : totalFlow.toString();
                    Double endFlow = "".equals(maxFlow.trim()) ? 0.00 : Double.valueOf(maxFlow);
                    Double startFlow = "".equals(startFlowStr.trim()) ? 0.00 : Double.valueOf(startFlowStr);
                    FlowRecordBean record = new FlowRecordBean();
                    record.setTime(time);
                    record.setStationId(stationId);
                    record.setTotalFlow(String.valueOf(endFlow));
                    double emissions = endFlow - startFlow;
                    emissions = emissions < 0 ? 0 : emissions;
                    record.setEmissions(String.format("%.2f", emissions));
                    record.setRange(String.valueOf(param.get("range")));
                    record.setLevelHeight(ZERO);
                    record.setSpeed(ZERO);
                    record.setFlow(ZERO);
                    Map<String, Object> avg = task.operationRecordService.getFlowAvg(param);
                    if (avg != null) {
                        if (avg.get(LEVEL) != null) {
                            record.setLevelHeight(String.valueOf(avg.get(LEVEL)));
                        }
                        if (avg.get(SPEED) != null) {
                            record.setSpeed(String.valueOf(avg.get(SPEED)));
                        }
                        if (avg.get(FLOW) != null) {
                            record.setFlow(String.valueOf(avg.get(FLOW)));
                        }
                    }
                    task.redisUtil.hset(StaticFinalWard.STATION + stationId, StaticFinalWard.DAY_FLOW, maxFlow);
                    task.realtimeService.insertFlowDayRecord(record);
                }
            } catch (Exception e) {
                log.error("流量日记录异常[" + station.getName() + "，ID：" + station.getId() + "]", e);
            }
        }
    }
}
