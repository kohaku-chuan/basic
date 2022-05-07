package com.micropower.basic.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.micropower.basic.common.dto.receive.RunningStateDto;
import com.micropower.basic.entity.DeviceBean;
import com.micropower.basic.entity.ExceptionRecordBean;
import com.micropower.basic.entity.RealtimeDataBean;
import com.micropower.basic.entity.StationBean;
import com.micropower.basic.netty.WaitingQueueService;
import com.micropower.basic.service.CompanyService;
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
 * @date 2022/5/6 10:09
 */
@Component
@Slf4j
public class RealtimeStateTask {

    private static RealtimeStateTask task;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private RealtimeService realtimeService;
    @Autowired
    private OperationRecordService operationRecordService;
    @Autowired
    private WaitingQueueService waitingQueueService;
    @Autowired
    private StationService stationService;

    @PostConstruct
    public void init() {
        task = this;
        task.redisUtil = this.redisUtil;
        task.realtimeService = this.realtimeService;
        task.companyService = this.companyService;
        task.waitingQueueService = this.waitingQueueService;
        task.stationService = this.stationService;
    }

    /**
     * @Author kohaku_C
     * @Description TODO 在线验证，超时未上传则离线
     * @Date 9:25  2021/3/16
     **/
    @Scheduled(cron = "0 0/1 * * * ?")
    private void checkOnline() {
        long nowTime = System.currentTimeMillis();
        //在线设备List
        Map<String, Object> param = new HashMap<>(2);
        param.put(StaticFinalWard.NETWORK, "在线");
        List<DeviceBean> list = task.companyService.getListByMap(param);
        param.clear();
        for (DeviceBean device : list) {
            int address = device.getAddress();
            String areaCode = device.getAreaCode();
            int overTime = device.getOverTime() * 60 * 1000;
            String receiverStr = device.getUploadTime();
            try {
                long receiverTime = DateUtil.parseStr2Date(receiverStr).getTime();
                //通讯超时，离线判定
                if (nowTime - receiverTime > overTime) {
                    //1、更新设备网络状态
                    StationBean station = PublicProcessing.getStationByAreaAddress(areaCode, address);
                    if (station != null) {
                        Integer stationId = station.getId();
                        //2、更新站点实时表
                        task.stationService.networkOffline(stationId);
                        //3、保存异常记录
                        task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(stationId, StaticFinalWard.NETWORK_WARN_NAME, "离线，最后通讯时间：" + receiverStr));
                        //4、短信发送
                        PublicProcessing.sendMessage(station, StaticFinalWard.NETWORK_WARN, StaticFinalWard.NETWORK_WARN_NAME, "设备离线，最后通讯时间：" + receiverStr);
                        task.redisUtil.hset(StaticFinalWard.STATION + stationId, StaticFinalWard.NETWORK, StaticFinalWard.OFF);
                    }
                }
            } catch (Exception e) {
                log.error("网络离线判定异常[ 设备：" + areaCode + "-" + address + "]", e);
            }
        }
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 更新实时数据，添加状态记录
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    private void insertUpdateRealtime() {
        String info;
        do {
            info = redisUtil.lrGet("realtimeData");
            if (info != null) {
                String nowTimeStr = DateUtil.parseDate2Str(new Date());
                RunningStateDto dto = JSON.parseObject(info, RunningStateDto.class);
                StationBean station = PublicProcessing.getStationByAreaAddress(dto.getAreaCode(), dto.getAddress());
                try {
                    RealtimeDataBean realtimeData = new RealtimeDataBean();
                    realtimeData.setUpdateTime(nowTimeStr);
                    //缓存版本号
                    redisUtil.hset("versionList", dto.getAreaCode() + dto.getAddress(), dto.getVersion());
                    realtimeData.setStationId(station.getId());
                    realtimeData.setLongitude(station.isGpsRead() ? dto.getLongitude() : station.getLongitude());
                    realtimeData.setLatitude(station.isGpsRead() ? dto.getLatitude() : station.getLatitude());
                    realtimeData.setNetwork("在线");
                    realtimeData.setRunState(dto.getRunState());
                    realtimeData.setFaultState(dto.getFaultState());
                    if (StaticFinalWard.TIME_ERROR.equals(dto.getFaultState())) {
                        task.redisUtil.hset(dto.getAreaCode() + dto.getAddress(), "timeSetting", StaticFinalWard.ON);
                    }
                    realtimeData.setRtcTime(dto.getRealTimeClock());
                    realtimeData.setInternalVoltage(dto.getInternalVoltage());
                    realtimeData.setExternalVoltage(dto.getExternalVoltage());
                    realtimeData.setSensorVoltage(dto.getSensorVoltage());
                    realtimeData.setTemp(dto.getTemp());
                    realtimeData.setHumidity(dto.getHumidity());
                    realtimeData.setTiltAngle(dto.getTiltAngle());
                    realtimeData.setRssi(dto.getRssi());
                    realtimeData.setRsrp(dto.getRsrp());
                    realtimeData.setSampleTime(CommonUtil.checkDate(dto.getSampleTime()) ? dto.getSampleTime() : nowTimeStr);
                    List<Map<String, Object>> rs485MapList = CommonUtil.toListMap(JSON.toJSONString(station.getDevice().getRs485()));
                    realtimeData.setDataState(rs485MapList.size() != dto.getR485ChannelValue().size() ? "异常" : "正常");
                    if (StaticFinalWard.ON.equals(station.getState())) {
                        globalWarnDeal(realtimeData, dto, station);
                    }
                    String lastTime = task.realtimeService.getLastTimeUploadTime(station.getId());
                    task.realtimeService.insertUpdateRealtime(realtimeData);
                    ThreadPoolUtil.execute(() -> task.waitingQueueService.readySendQueue(dto.getCommunicationMode(), dto.getAreaCode(), dto.getAddress()));
                    if (!StaticFinalWard.ON.equals(String.valueOf(redisUtil.hget(StaticFinalWard.STATION + realtimeData.getStationId(), StaticFinalWard.NETWORK)))) {
                        task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(realtimeData.getStationId(), "网络", "上线"));
                        task.redisUtil.hset(StaticFinalWard.STATION + realtimeData.getStationId(), StaticFinalWard.NETWORK, StaticFinalWard.ON);
                    }
                    int interval = lastTime == null ? 0 : CommonUtil.dateDiff(lastTime, nowTimeStr);
                    if (interval <= 0 || interval > 15) {
                        task.realtimeService.insertStateRecord(realtimeData);
                    }
                } catch (Exception e) {
                    log.error("实时记录异常[" + station.getName() + "，ID：" + station.getId() + "，设备：" + dto.getAreaCode() + "-" + dto.getAddress() + "]", e);
                }
            }
        } while (info != null);
    }

    /**
     * @param
     * @description 全局预警处理
     * @author Kohaku_川
     * @date 2021/10/27 10:20
     */
    private void globalWarnDeal(RealtimeDataBean realtimeData, RunningStateDto
            dto, StationBean station) {
        Map<String, String> globalWarn = task.realtimeService.getGlobalWarn();
        if (globalWarn != null) {
            Map<String, Object> voltage = JSON.parseObject(globalWarn.get("voltage"), new TypeReference<HashMap<String, Object>>() {
            });
            Map<String, Object> sensorVoltage = JSON.parseObject(globalWarn.get("sensor_voltage"), new TypeReference<HashMap<String, Object>>() {
            });
            Map<String, Object> humidity = JSON.parseObject(globalWarn.get("humidity"), new TypeReference<HashMap<String, Object>>() {
            });
            double vMin = Double.parseDouble(voltage.get("min").toString());
            double vMax = Double.parseDouble(voltage.get("max").toString());
            double svMin = Double.parseDouble(sensorVoltage.get("min").toString());
            double svMax = Double.parseDouble(sensorVoltage.get("max").toString());
            double hMin = Double.parseDouble(humidity.get("min").toString());
            double hMax = Double.parseDouble(humidity.get("max").toString());
            boolean voltageWarn = Double.valueOf(dto.getInternalVoltage()) < vMin || Double.valueOf(dto.getInternalVoltage()) > vMax;
            realtimeData.setVoltageWarn(voltageWarn);
            if (voltageWarn && task.redisUtil.hget(StaticFinalWard.VOLTAGE_WARN, String.valueOf(station.getId())) != null
                    && StaticFinalWard.OFF.equals(task.redisUtil.hget(StaticFinalWard.VOLTAGE_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 设备内电压 ] 预警值：" + dto.getInternalVoltage()));
            }
            task.redisUtil.hset(StaticFinalWard.VOLTAGE_WARN, String.valueOf(station.getId()), voltageWarn ? StaticFinalWard.ON : StaticFinalWard.OFF);

            boolean sensorVoltageWarn = StaticFinalWard.ON.equals(station.getDevice().getIsSensor()) && (Double.valueOf(dto.getSensorVoltage()) < svMin || Double.valueOf(dto.getSensorVoltage()) > svMax);
            realtimeData.setSensorVoltageWarn(sensorVoltageWarn);
            if (sensorVoltageWarn && task.redisUtil.hget(StaticFinalWard.SENSOR_VOLTAGE_WARN, String.valueOf(station.getId())) != null
                    && StaticFinalWard.OFF.equals(task.redisUtil.hget(StaticFinalWard.SENSOR_VOLTAGE_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 传感器电压 ] 预警值：" + dto.getSensorVoltage()));
            }
            task.redisUtil.hset(StaticFinalWard.SENSOR_VOLTAGE_WARN, String.valueOf(station.getId()), sensorVoltageWarn ? StaticFinalWard.ON : StaticFinalWard.OFF);

            boolean humidityWarn = Double.valueOf(dto.getHumidity()) < hMin || Double.valueOf(dto.getHumidity()) > hMax;
            realtimeData.setHumidityWarn(humidityWarn);
            if (humidityWarn && task.redisUtil.hget(StaticFinalWard.HUMIDITY_WARN, String.valueOf(station.getId())) != null
                    && StaticFinalWard.OFF.equals(task.redisUtil.hget(StaticFinalWard.HUMIDITY_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 湿度 ] 预警值：" + dto.getHumidity()));
            }
            task.redisUtil.hset(StaticFinalWard.HUMIDITY_WARN, String.valueOf(station.getId()), humidityWarn ? StaticFinalWard.ON : StaticFinalWard.OFF);
        }
    }
}