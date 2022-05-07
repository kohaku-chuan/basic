package com.micropower.basic.timer;

import com.alibaba.fastjson.JSON;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.micropower.basic.common.dto.receive.HistoryChildRecord;
import com.micropower.basic.entity.*;
import com.micropower.basic.forward.c0.AnalogLevelForward;
import com.micropower.basic.forward.c0.FlowForwardC0;
import com.micropower.basic.forward.c0.LevelForward;
import com.micropower.basic.forward.c0.Rs485LevelForward;
import com.micropower.basic.forward.common212.FlowForward;
import com.micropower.basic.service.CompanyService;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.service.RealtimeService;
import com.micropower.basic.netty.WaitingQueueService;
import com.micropower.basic.service.StationService;
import com.micropower.basic.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.*;

/**
 * @Date: 2021/2/20 10:28
 * @Description: //TODO数据处理任务
 * @Author:Kohaku_川
 **/
@Component
@Slf4j
public class PublicProcessing {

    private static PublicProcessing task;

    @Value("${spring.profiles.active}")
    private static String active;

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
        task.companyService = this.companyService;
        task.realtimeService = this.realtimeService;
        task.waitingQueueService = this.waitingQueueService;
        task.stationService = this.stationService;
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加流量记录
     * @Date 13:15  2022/5/6
     * @Param
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    private void insertFlowRecord() {
        String info;
        do {
            info = redisUtil.lrGet("flowRecord");
            if (info != null) {
                HistoryChildRecord record = JSON.parseObject(info, HistoryChildRecord.class);
                StationBean station = PublicProcessing.getStationByAreaAddress(record.getAreaCode(), record.getAddress());
                try {
                    DeviceBean device = station.getDevice();
                    List<Map<String, Object>> realValueList = record.getRealValueList();
                    String flow = "";
                    String speed;
                    String levelHeight;
                    String emissions = "";
                    String totalFlow = "";
                    int interval = 5;
                    Object lastTimeObj = task.redisUtil.hget("flowMinSampleTime", StaticFinalWard.STATION + station.getId());
                    if (lastTimeObj != null) {
                        String lastTimeStr = lastTimeObj.toString();
                        interval = CommonUtil.dateDiff(lastTimeStr, record.getSampleTime());
                    }
                    interval = interval <= 0 ? 5 : interval;
                    // 液位高度
                    levelHeight = isValid(realValueList, device.getLevelHeightName(), record.getLevel());

                    // 流速
                    speed = isValid(realValueList, device.getSpeedName(), record.getLevel());

                    //累积流量基础值
                    double totalFlowBasic = 0.00;
                    Map<String, Object> flowBasicMap = realtimeService.getTotalFlowBasic(station.getId());

                    //时间间隔
                    if (flowBasicMap != null) {
                        String baseFlowStr = String.valueOf(flowBasicMap.get("flow_base"));
                        totalFlowBasic = ("null".equals(baseFlowStr) || "异常".equals(baseFlowStr) || baseFlowStr.isEmpty()) ? 0.00 : Double.valueOf(baseFlowStr);
                    }

                    // 原始值上传
                    if ("0".equals(device.getFlowCount())) {
                        // 瞬时流量
                        flow = isValid(realValueList, device.getFlowName(), record.getLevel());

                        //累计流量
                        String totalFlowName = device.getTotalFlowName();
                        for (Map<String, Object> map : realValueList) {
                            if (map.containsValue(totalFlowName)) {
                                totalFlow = map.get(StaticFinalWard.VALUE).toString();
                                if (!"异常".equals(totalFlow)) {
                                    emissions = String.format("%.2f", Double.valueOf(totalFlow) - totalFlowBasic);
                                } else {
                                    emissions = "0.00";
                                }
                                realValueList.remove(map);
                                break;
                            }
                        }

                        //公式计算
                    } else {
                        //淤泥高度
                        double slitHeight = Double.parseDouble(device.getSiltHeight());
                        //直径/底边宽度
                        String diameter = device.getPipeDiameter();
                        //限高
                        String pipeHeight = device.getPipeHeight();
                        //液位超过限高，按限高算
                        String countLevelHeight = levelHeight;
                        if (Double.valueOf(levelHeight) > Double.valueOf(pipeHeight)) {
                            countLevelHeight = pipeHeight;
                        }
                        //液位为负值，计为0
                        if (Double.valueOf(levelHeight) < 0) {
                            countLevelHeight = "0";
                        }
                        //真实高度
                        double height = Double.valueOf(countLevelHeight) - slitHeight;
                        //截面积
                        double area = 0;
                        double flowValue;
                        double emissionsValue;
                        double totalFlowValue;
                        //管道形状
                        switch (device.getPipeShape()) {
                            //圆型管道
                            case "0":
                                if (height > 0) {
                                    area = CommonUtil.getCircleSection(Double.valueOf(diameter), height);
                                }
                                area = area < 0 ? 0 : area;
                                //瞬时流量
                                flowValue = area * Double.valueOf(speed) * 3600;
                                flow = String.format("%.3f", flowValue);
                                //排放量
                                emissionsValue = flowValue * interval / 60;
                                emissions = String.format("%.2f", emissionsValue);
                                //累计流量
                                totalFlowValue = totalFlowBasic + emissionsValue;
                                totalFlow = String.format("%.2f", totalFlowValue);
                                break;
                            //矩形
                            case "1":
                                if (height > 0) {
                                    area = height * Double.valueOf(diameter);
                                }
                                area = area < 0 ? 0 : area;
                                //瞬时流量
                                flowValue = area * Double.valueOf(speed) * 3600;
                                flow = String.format("%.3f", flowValue);
                                //排放量
                                emissionsValue = flowValue * interval / 60;
                                emissions = String.format("%.2f", emissionsValue);
                                //累计流量
                                totalFlowValue = totalFlowBasic + emissionsValue;
                                totalFlow = String.format("%.2f", totalFlowValue);
                                break;
                            //三角形
                            case "2":
                                //流量系数
                                double triangleCoefficient = Double.parseDouble(device.getTriangleCoefficient());
                                height = height > 0 ? height : 0;
                                //瞬时流量
                                flowValue = triangleCoefficient * Math.pow(height, 2.5);
                                flow = String.format("%.3f", flowValue);
                                //排放量
                                emissionsValue = flowValue * interval / 60;
                                emissions = String.format("%.2f", emissionsValue);
                                //累计流量
                                totalFlowValue = totalFlowBasic + emissionsValue;
                                totalFlow = String.format("%.2f", totalFlowValue);
                                break;
                            default:
                                break;
                        }
                    }
                    emissions = (!"".equals(emissions) && Double.valueOf(emissions) < 0) ? "0.0" : emissions;
                    if (CommonUtil.isDouble(totalFlow)) {
                        task.realtimeService.updateFlowBase(station.getId(), totalFlow);
                    }
                    if (record.isUpdateToRealtime()) {
                        RealtimeDataBean realtime = new RealtimeDataBean();
                        realtime.setStationId(station.getId());
                        realtime.setLevelHeight(levelHeight);
                        realtime.setSpeed(speed);
                        realtime.setFlow(flow);
                        realtime.setTotalFlow(totalFlow);
                        realtime.setFlowBase(totalFlow);
                        realtime.setEmissions(emissions);
                        task.realtimeService.insertUpdateRealtime(realtime);
                    }
                    task.realtimeService.insertFlowMinRecord(new FlowRecordBean(station.getId(), record.getSampleTime(), levelHeight, speed, flow, totalFlow, emissions, null));
                    task.redisUtil.hset("flowMinSampleTime", StaticFinalWard.STATION + station.getId(), record.getSampleTime());
                } catch (Exception e) {
                    log.error("流量记录异常[" + station.getName() + "，ID：" + station.getId() + "，设备：" + record.getAreaCode() + "-" + record.getAddress() + "]", e);
                }
            }
        } while (info != null);
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加雨量记录
     * @Date 13:15  2022/5/6
     * @Param
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    private void insertRainRecord() {
        String info;
        do {
            info = redisUtil.lrGet("rainRecord");
            if (info != null) {
                HistoryChildRecord record = JSON.parseObject(info, HistoryChildRecord.class);
                StationBean station = PublicProcessing.getStationByAreaAddress(record.getAreaCode(), record.getAddress());
                List<Map<String, Object>> realValueList = record.getRealValueList();
                try {
                    Map<String, Object> pulseBasicMap = realtimeService.getPulseBasic(station.getId());
                    DeviceBean device = station.getDevice();
                    // 雨量脉冲值
                    String rainfallPulseStr = isValid(realValueList, device.getRainfallPulseName(), record.getLevel());

                    //雨量脉冲基础值
                    double pulseBasic = 0.00;

                    //时间间隔
                    int min = 5;
                    Object lastTimeObj = task.redisUtil.hget("rainMinSampleTime", StaticFinalWard.STATION + station.getId());
                    if (lastTimeObj != null) {
                        String lastTimeStr = lastTimeObj.toString();
                        min = CommonUtil.dateDiff(lastTimeStr, record.getSampleTime());
                    }
                    if (pulseBasicMap != null && pulseBasicMap.containsKey("pulse_base")) {
                        String pulseBaseStr = pulseBasicMap.get("pulse_base").toString();
                        pulseBasic = ("null".equals(pulseBaseStr) || pulseBaseStr.isEmpty()) ? 0.00 : Double.valueOf(pulseBaseStr);
                    }
                    min = min <= 0 ? 5 : min;
                    //降雨量脉冲原始值
                    double pulse = Double.parseDouble(rainfallPulseStr);
                    //降雨量脉冲计算
                    double originPulse = pulse;
                    if (Double.valueOf(rainfallPulseStr) < pulseBasic) {
                        originPulse = pulse + 65535 - pulseBasic;
                    }
                    //降雨量
                    double rainfall = (originPulse - pulseBasic) * 0.2;
                    //降雨强度
                    double intensity = 0.00;
                    if (rainfall > 0) {
                        intensity = rainfall / min;
                    }
                    RealtimeDataBean realtime = new RealtimeDataBean();
                    realtime.setStationId(station.getId());
                    realtime.setRainfall(String.format("%.3f", rainfall));
                    realtime.setSampleTime(record.getSampleTime());
                    realtime.setPulseBase(String.valueOf(originPulse));
                    realtime.setPulseUpload(String.valueOf(pulse));
                    realtime.setPulseCount(String.valueOf(originPulse));
                    realtime.setRainfallIntensity(String.format("%.3f", intensity));
                    if (record.isUpdateToRealtime()) {
                        task.realtimeService.insertUpdateRealtime(realtime);
                    }
                    task.realtimeService.insertRainfallMinRecord(realtime);
                    task.redisUtil.hset("rainMinSampleTime", StaticFinalWard.STATION + station.getId(), record.getSampleTime());
                } catch (Exception e) {
                    log.error("雨量记录异常[" + station.getName() + "，ID：" + station.getId() + "，设备：" + record.getAreaCode() + "-" + record.getAddress() + "]", e);
                }
            }
        } while (info != null);
    }

    private void dataForwarding(StationBean station, RealtimeDataBean
            realtimeData, List<Map<String, Object>> rs485RealList, List<Map<String, Object>> analogRealList) {
        Integer stationId = station.getId();
        List<Map<String, Object>> configureList = task.companyService.getStationForward(stationId);
        if (!configureList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : configureList) {
                String type = stringObjectMap.get("type").toString();
                String ip = stringObjectMap.get("ip").toString();
                int port = Integer.parseInt(stringObjectMap.get("port").toString());
                int forwardingAddress = Integer.parseInt(stringObjectMap.get("forwarding_address").toString());
                switch (type) {
                    //超声波液位
                    case "1":
                        LevelForward.forward(stationId, ip, port, forwardingAddress, realtimeData.getOutletLevel());
                        break;
                    //永康212协议
                    case "2":
                        FlowForward.forward(stationId, forwardingAddress, ip, port, realtimeData.getFlow(),
                                realtimeData.getSpeed(), realtimeData.getLevelHeight(), realtimeData.getTotalFlow(), realtimeData.getTiltAngle(),
                                realtimeData.getInternalVoltage(), realtimeData.getSensorVoltage(), realtimeData.getTemp());
                        break;
                    case "3":
                        FlowForwardC0.forward(new FlowForwardBean(stationId, ip, port, forwardingAddress,
                                realtimeData.getLevelHeight(), realtimeData.getSpeed(), realtimeData.getFlow(), realtimeData.getTotalFlow()));
                        break;
                    //rs485液位
                    case "4":
                        Rs485LevelForward.forward(stationId, ip, port, forwardingAddress, rs485RealList);
                        break;
                    //模拟通道液位
                    case "5":
                        AnalogLevelForward.forward(stationId, ip, port, forwardingAddress, analogRealList);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    static void sendSuperAdminLevelError(StationBean station) {
        List<Map<String, Object>> superAdminList = task.companyService.getSuperAdminPhone(StaticFinalWard.LEVEL_WARN);
        List<WarningMsg> sendList = getSendMsgList(superAdminList, station, StaticFinalWard.LEVEL_WARN_NAME, StaticFinalWard.LEVEL_WARN, "超声波液位测量故障(-0.01)");
        if (!sendList.isEmpty()) {
            sendMessageServer(sendList);
        }
    }

    private static List<WarningMsg> getSendMsgList(List<Map<String, Object>> userList, StationBean
            station, String warnType, String keyWord, String content) {
        List<WarningMsg> sendList = new ArrayList<>();
        if (!userList.isEmpty()) {
            for (Map<String, Object> userMap : userList) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                boolean send = (hours < 22 && hours > 7) || Boolean.valueOf(userMap.get("night").toString());
                if (send) {
                    String key = station.getId() + "_" + userMap.get("id") + "_" + keyWord;
                    Object wait = task.redisUtil.hget(key, warnType);
                    if (wait == null) {
                        int interval = Integer.parseInt(userMap.get("interval").toString());
                        sendList.add(new WarningMsg(userMap.get("number").toString(), warnType, station, key, interval, content));
                    }
                }
            }
        }
        return sendList;
    }

    private void writeToBuoyStation(Integer id, List<Map<String, Object>> realValueList) {
        try {
            Map<String, Object> config = task.companyService.getBuoyStationConfigure(id);
            if (config != null) {
                String codName = config.get("cod_name").toString();
                String phName = config.get("ph_name").toString();
                String ssName = config.get("ss_name").toString();
                String nh3Name = config.get("nh3_name").toString();
                double cod = 0.0;
                double ph = 0.0;
                double ss = 0.0;
                double nh3 = 0.0;
                for (Map<String, Object> map : realValueList) {
                    String name = map.get("name").toString();
                    String valueStr = map.get(StaticFinalWard.VALUE).toString();
                    Double value = "异常".equals(valueStr) ? 0.0 : Double.valueOf(valueStr);
                    if (codName.equals(name)) {
                        cod = value;
                    } else if (phName.equals(name)) {
                        ph = value;
                    } else if (ssName.equals(name)) {
                        ss = value;
                    } else if (nh3Name.equals(name)) {
                        nh3 = value;
                    }
                }
                new BuoyStationApi(id.toString(), cod, ph, ss, nh3).getRobotResult();
            }
        } catch (Exception e) {
            log.error("浮标站[" + id + "]写值异常", e);
        }
    }

    private String isValid(List<Map<String, Object>> list, String name, String level) {
        String value = "0.00";
        switch (name) {
            case "无":
                value = "--";
                break;
            case "水深液位":
                value = ("异常".equals(level) || "".equals(level)) ? "0.00" : level;
                break;
            default:
                for (Map<String, Object> map : list) {
                    if (map.containsValue(name)) {
                        value = map.get(StaticFinalWard.VALUE).toString();
                        value = ("异常".equals(value) || "".equals(value)) ? "0.00" : value;
                        list.remove(map);
                        break;
                    }
                }
                break;
        }
        return value;
    }

    /**
     * @param station  站点
     * @param warnType 预警类型（超声波液位预警，网络预警，一级预警，二级预警）
     * @param keyWord  异常名称
     * @description TODO 发送短信服务方法
     * @author Kohaku_川
     * @date 2022/4/29 14:41
     */
    static void sendMessage(StationBean station, String warnType, String keyWord, String content) {
        if (!StaticFinalWard.DEV_MODE.equals(active)) {
            List<Map<String, Object>> superAdminList = task.companyService.getSuperAdminPhone(warnType);
            List<Map<String, Object>> normalUserList = task.companyService.getNormalUserPhone(station.getBlockUrl(), warnType);
            List<Map<String, Object>> userList = new ArrayList<>();
            userList.addAll(superAdminList);
            userList.addAll(normalUserList);
            List<WarningMsg> sendList = getSendMsgList(userList, station, warnType, keyWord, content);
            if (!sendList.isEmpty()) {
                sendMessageServer(sendList);
            }
        }
    }

    private static void sendMessageServer(List<WarningMsg> sendList) {
        for (WarningMsg warningMsg : sendList) {
            StationBean station = warningMsg.getStation();
            String[] numbers = {warningMsg.getNumber()};
            String[] params = {station.getName(), warningMsg.getKey(), warningMsg.getContent()};
            SmsMultiSender sender = new SmsMultiSender(1400293649, "82e02452bc16d9b7992568388fad642d");
            SmsMultiSenderResult result = null;
            try {
                result = sender.sendWithParam("86", numbers,
                        1385584, params, "小桥流水环境科技", "", "");
            } catch (HTTPException | IOException | com.github.qcloudsms.httpclient.HTTPException e) {
                e.printStackTrace();
            }
            Map<String, Object> recordMap = new HashMap<>(4);
            recordMap.put("content", "【" + station.getName() + "】站点，" + warningMsg.getKey() + "，" + warningMsg.getContent() + "，请关注该站点的数据变化！");
            recordMap.put("stationId", station.getId());
            recordMap.put("type", warningMsg.getKey());
            if (Objects.requireNonNull(result).result == 0) {
                recordMap.put("feedback", "发送成功");
                task.redisUtil.hset(warningMsg.getKey(), warningMsg.getWarnType(), "1", warningMsg.getInterval() * 60 * 60);
            } else {
                recordMap.put("feedback", "错误码：" + result.result);
            }
            task.operationRecordService.insertMessageRecord(recordMap);
        }
    }

    public static StationBean getStationByAreaAddress(String areaCode, Integer address) {
        Map<String, Object> param = new HashMap<>(2);
        param.put(StaticFinalWard.AREA_CODE, areaCode);
        param.put(StaticFinalWard.ADDRESS, address);
        return task.stationService.getStationByMap(param);
    }

}
