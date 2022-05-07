package com.micropower.basic.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.micropower.basic.common.dto.receive.HistoryChildRecord;
import com.micropower.basic.common.dto.receive.ValueRecordDto;
import com.micropower.basic.entity.DeviceBean;
import com.micropower.basic.entity.ExceptionRecordBean;
import com.micropower.basic.entity.StationBean;
import com.micropower.basic.netty.WaitingQueueService;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.service.RealtimeService;
import com.micropower.basic.service.StationService;
import com.micropower.basic.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/4/29 14:30
 */
@Slf4j
@Component
public class DataRecordProcessingTask {

    private static DataRecordProcessingTask task;

    private @Autowired
    RedisUtil redisUtil;

    private @Autowired
    RealtimeService realtimeService;

    private @Autowired
    StationService stationService;

    private @Autowired
    OperationRecordService operationRecordService;

    private @Autowired
    WaitingQueueService waitingQueueService;

    @PostConstruct
    public void init() {
        task = this;
        task.realtimeService = this.realtimeService;
        task.redisUtil = this.redisUtil;
        task.stationService = this.stationService;
        task.operationRecordService = this.operationRecordService;
        task.waitingQueueService = this.waitingQueueService;
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加并反馈周期记录,实时更新、预警、转发最新记录
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "0/10 * * * * ?")
    private void insertCycleRecord() {
        String info;
        do {
            info = redisUtil.lrGet("valueRecord");
            if (info != null) {
                ValueRecordDto dto = JSON.parseObject(info, ValueRecordDto.class);
                try {
                    StationBean station = PublicProcessing.getStationByAreaAddress(dto.getAreaCode(), dto.getAddress());
                    if (station != null) {
                        Map<String, Object> realtimeMap = realtimeService.getRealTimeById(station.getId());
                        Date sampleTime = DateUtil.parseStr2Date(realtimeMap.get("sample_time").toString());
                        DeviceBean device = station.getDevice();
                        Date normalTime = DateUtil.parseStr2Date(station.getNormalTime());
                        Date abnormalTime = DateUtil.parseStr2Date(station.getAbnormalTime());
                        List<Map<String, Object>> rs485MapList = CommonUtil.toListMap(JSON.toJSONString(device.getRs485()));
                        List<Map<String, Object>> analogMapList = CommonUtil.toListMap(JSON.toJSONString(device.getAnalog()));
                        for (HistoryChildRecord record : dto.getValueList()) {
                            Date sampleDate = DateUtil.parseStr2Date(record.getSampleTime());
                            boolean error1 = StaticFinalWard.ON.equals(station.getState()) && sampleDate.before(normalTime);
                            boolean error2 = StaticFinalWard.OFF.equals(station.getState()) && sampleDate.after(abnormalTime);
                            boolean error3 = rs485MapList.size() != record.getR485ChannelValue().size();
                            if (error1 || error2 || error3) {
                                continue;
                            }
                            Map<String, Object> runMap = new HashMap<>();
                            runMap.put("stationId", station.getId());
                            runMap.put("sampleTime", record.getSampleTime());
                            levelCheck(station, runMap, record);
                            int error = 0;
                            //遍历rs485因子获取真实值
                            List<String> valueStrList = new ArrayList<>();
                            valueStrList.addAll(record.getR485ChannelValue());
                            valueStrList.addAll(record.getAnalogValue());
                            List<Map<String, Object>> valueMapList = new ArrayList<>();
                            valueMapList.addAll(rs485MapList);
                            valueMapList.addAll(analogMapList);
                            for (int a = 0; a < valueMapList.size(); a++) {
                                Map<String, Object> valueMap = valueMapList.get(a);
                                String value = valueStrList.get(a);
                                valueMap.put(StaticFinalWard.VALUE, value);
                                if (!StaticFinalWard.WRONG_DATA.equals(value)) {
                                    double compensation = Double.parseDouble(valueMap.get(StaticFinalWard.COMPENSATION).toString());
                                    double filterMin = Double.parseDouble(valueMap.get(StaticFinalWard.MIN).toString());
                                    double filterMax = Double.parseDouble(valueMap.get(StaticFinalWard.MAX).toString());
                                    double realValue = Double.valueOf(value) + compensation;
                                    double dealValue = realValue;
                                    if (realValue < filterMin) {
                                        dealValue = filterMin;
                                    } else if (realValue > filterMax) {
                                        dealValue = filterMax;
                                    }
                                    if (dealValue != realValue) {
                                        error++;
                                    }
                                    valueMap.put(StaticFinalWard.VALUE, String.format("%.3f", dealValue));
                                    if (record.isUpdateToRealtime() && StaticFinalWard.ON.equals(station.getState())) {
                                        //1级预警
                                        double warn1Min = Double.parseDouble(valueMap.get("warn1Min").toString());
                                        double warn1Max = Double.parseDouble(valueMap.get("warn1Max").toString());
                                        if (realValue < warn1Min || realValue > warn1Max) {
                                            task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), StaticFinalWard.ONE_LEVEL_WARN_NAME, "[ " + valueMap.get("name") + " ] 测量值：" + realValue));
                                            PublicProcessing.sendMessage(station, StaticFinalWard.ONE_LEVEL_WARN, valueMap.get("name").toString(), "测量值:" + realValue);
                                        }
                                        //2级预警
                                        double warn2Min = Double.parseDouble(valueMap.get("warn2Min").toString());
                                        double warn2Max = Double.parseDouble(valueMap.get("warn2Max").toString());
                                        if (realValue < warn2Min || realValue > warn2Max) {
                                            task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), StaticFinalWard.TWO_LEVEL_WARN_NAME, "[ " + valueMap.get("name") + " ] 测量值：" + realValue));
                                            PublicProcessing.sendMessage(station, StaticFinalWard.TWO_LEVEL_WARN, valueMap.get("name").toString(), "测量值:" + realValue);
                                        }
                                    }
                                }
                            }
                            runMap.put("valueList", valueMapList.toString());
                            runMap.put("error", error > 0);
                            task.operationRecordService.insertCycleRecord(runMap);
                            record.setRealValueList(valueMapList);
                            record.setUpdateToRealtime(sampleTime.equals(DateUtil.parseStr2Date(record.getSampleTime())));
                            // 流量记录
                            if (StaticFinalWard.ON.equals(station.getDevice().getIsFlow())) {
                                task.redisUtil.llSet("flowRecord", JSON.toJSONString(record, SerializerFeature.IgnoreErrorGetter));
                            }
                            // 雨量记录
                            if (StaticFinalWard.ON.equals(station.getDevice().getIsRainfall())) {
                                task.redisUtil.llSet("rainRecord", JSON.toJSONString(record, SerializerFeature.IgnoreErrorGetter));
                            }
                        }
                        ThreadPoolUtil.execute(() -> task.waitingQueueService.feedbackCycleUpload(dto.getCommunicationMode(), dto.getAreaCode(), dto.getAddress()));
                    }
                } catch (Exception e) {
                    log.error("周期记录异常[" + dto.getAreaCode() + "-" + dto.getAddress() + "]", e);
                }
            }
        } while (info != null);
    }

    private void levelCheck(StationBean station, Map<String, Object> map, HistoryChildRecord record) {
        //对开启“水深液位”的设备进行数据处理
        if (StaticFinalWard.ON.equals(station.getDevice().getIsLevel())) {
            boolean onlySendSuperAdmin = false;
            boolean levelWarn = false;
            if (StaticFinalWard.WRONG_DATA.equals(record.getLevel()) || StaticFinalWard.FAULT_DATA.equals(record.getLevel())) {
                onlySendSuperAdmin = record.isUpdateToRealtime();
                map.put("levelState", "故障");
                double min = Double.parseDouble(station.getDevice().getLevelFakeMin());
                double max = Double.parseDouble(station.getDevice().getLevelFakeMax());
                //范围内随机生成数据
                map.put("outletLevel", CommonUtil.getRandomNumber(min, max));
                //正常值
            } else {
                map.put("levelState", "正常");
                double warnValue = Double.parseDouble(station.getDevice().getLevelWarn());
                if (Double.valueOf(record.getLevel()) > warnValue) {
                    levelWarn = record.isUpdateToRealtime();
                }
                map.put("outletLevel", record.getLevel());
            }
            if (StaticFinalWard.ON.equals(station.getState()) && levelWarn) {
                //给该站点数据权限拥有者发送超标预警短信
                PublicProcessing.sendMessage(station, StaticFinalWard.LEVEL_WARN, StaticFinalWard.LEVEL_WARN_NAME, "液位超高预警，液位：" + record.getLevel() + "m");
            }
            //给超级管理员发送状态异常短信
            if (onlySendSuperAdmin) {
                PublicProcessing.sendSuperAdminLevelError(station);
            }
        }
    }
}
