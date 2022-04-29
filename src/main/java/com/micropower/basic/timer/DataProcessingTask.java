package com.micropower.basic.timer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.micropower.basic.common.dto.receive.HistoryChildRecord;
import com.micropower.basic.common.dto.receive.RunningStateDto;
import com.micropower.basic.common.dto.receive.ValueRecordDto;
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
public class DataProcessingTask {

    private static final String AREA_CODE = "area_code";
    private static final String ADDRESS = "address";
    private static final String NETWORK = "network";
    private static final String STATION = "station";
    public static final String STATE = "state";
    private static final String WRONG_DATA = "异常";
    public static final String ON = "1";
    private static final String OFF = "0";
    private static final String DEV_MODE = "dev";
    private static final String VOLTAGE_WARN = "voltage_warn";
    private static final String SENSOR_VOLTAGE_WARN = "sensor_voltage_warn";
    private static final String HUMIDITY_WARN = "humidity_warn";
    private static final String NETWORK_WARN_NAME = "网络预警";
    private static final String NETWORK_WARN = "network_warn";
    private static final String LEVEL_WARN_NAME = "水深液位预警";
    private static final String LEVEL_WARN = "level_warn";
    private static final String ONE_LEVEL_WARN_NAME = "1级预警";
    private static final String ONE_LEVEL_WARN = "one_level_warn";
    private static final String TWO_LEVEL_WARN_NAME = "2级预警";
    private static final String TWO_LEVEL_WARN = "two_level_warn";
    private static final String IS_FLOW = "is_flow";


    private static DataProcessingTask task;

    @Value("${spring.profiles.active}")
    public String active;

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
     * @Author kohaku_C
     * @Description TODO 在线验证，超时未上传则离线
     * @Date 9:25  2021/3/16
     **/
    @Scheduled(cron = "0 0/1 * * * ?")
    private void checkOnline() {
        long nowTime = System.currentTimeMillis();
        //在线设备List
        Map<String, Object> param = new HashMap<>(2);
        param.put(NETWORK, "在线");
        List<DeviceBean> list = task.companyService.getListByMap(param);
        param.clear();
        for (DeviceBean device : list) {
            Integer address = device.getAddress();
            String areaCode = device.getAreaCode();
            int overTime = device.getOverTime() * 60 * 1000;
            String receiverStr = device.getUploadTime();
            try {
                long receiverTime = DateUtil.parseStr2Date(receiverStr).getTime();
                //通讯超时，离线判定
                if (nowTime - receiverTime > overTime) {
                    //1、更新设备网络状态
                    task.realtimeService.deviceOffline(areaCode, address);
                    param.put(AREA_CODE, areaCode);
                    param.put(ADDRESS, address);
                    StationBean station = task.stationService.getStationByMap(param);
                    if (station != null) {
                        Integer stationId = station.getId();
                        //2、更新站点实时表
                        task.stationService.networkOffline(stationId);
                        //3、保存异常记录
                        task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(stationId, NETWORK_WARN_NAME, "离线，最后通讯时间：" + receiverStr));
                        //4、短信发送
                        sendMessage(station, NETWORK_WARN, NETWORK, "设备离线，最后通讯时间：" + receiverStr);
                        task.redisUtil.hset(STATION + stationId, NETWORK, OFF);
                    }
                }
            } catch (Exception e) {
                log.error("网络判定异常[" + areaCode + "-" + address + "]", e);
            }
        }
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 更新实时数据，添加状态记录，水质记录
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "0/5 * * * * ?")
    private void insertUpdateRealtime() {
        List<String> runList = new ArrayList<>();
        String info;
        do {
            info = redisUtil.lrGet("realtimeData");
            if (info != null) {
                runList.add(info);
            }
        } while (info != null);
        if (!runList.isEmpty()) {
            String nowTimeStr = DateUtil.parseDate2Str(new Date());
            for (String runStr : runList) {
                RunningStateDto dto = JSON.parseObject(runStr, RunningStateDto.class);
                try {
                    Map<String, Object> param = new HashMap<>(2);
                    param.put(AREA_CODE, dto.getAreaCode());
                    param.put(ADDRESS, dto.getAddress());
                    StationBean station = task.stationService.getStationByMap(param);
                    if (station != null) {
                        //通讯间隔
                        String lastTime = task.realtimeService.getLastTimeUploadTime(station.getId());
                        int interval = lastTime == null ? 0 : CommonUtil.dateDiff(lastTime, nowTimeStr);

                        //离线后上线判断
                        if (!ON.equals(String.valueOf(redisUtil.hget(STATION + station.getId(), NETWORK)))) {
                            task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "网络", "上线"));
                            task.redisUtil.hset(STATION + station.getId(), NETWORK, ON);
                        }
                        RealtimeDataBean realtimeData = new RealtimeDataBean();
                        realtimeData.setUpdateTime(nowTimeStr);

                        //1.数据初步处理，进入指令下发队列
                        dataPreprocessing(station, realtimeData, dto);

                        //2.超声波液位校验
                        levelCheck(station, realtimeData, dto);

                        //3.正常运行状态下，全局预警判断
                        if (ON.equals(station.getState())) {
                            globalWarnDeal(realtimeData, dto, station);
                        }

                        //4.上下位机配置校验是否匹配（串口因子数是否统一）
                        List<Map<String, Object>> rs485MapList = CommonUtil.toListMap(JSON.toJSONString(station.getDevice().getRs485()));
                        List<Map<String, Object>> analogMapList = CommonUtil.toListMap(JSON.toJSONString(station.getDevice().getAnalog()));
                        //上下位机串口因子配置如果不匹配，不更新因子数据
                        if (rs485MapList.size() != dto.getR485ChannelValue().size()) {
                            realtimeData.setDataState("异常");
                            task.realtimeService.insertUpdateRealtime(realtimeData);
                            continue;
                        } else {
                            realtimeData.setDataState("正常");
                        }

                        //5.因子进行补偿，预警
                        List<Map<String, Object>> realValueList = new ArrayList<>();
                        //遍历rs485因子处理数据
                        List<Map<String, Object>> rs485RealList = new ArrayList<>();
                        for (int a = 0; a < rs485MapList.size(); a++) {
                            rs485RealList.add(valueDeal(rs485MapList.get(a), dto.getR485ChannelValue().get(a), station));
                        }
                        //遍历模拟通道因子处理数据
                        List<Map<String, Object>> analogRealList = new ArrayList<>();
                        for (int a = 0; a < analogMapList.size(); a++) {
                            analogRealList.add(valueDeal(analogMapList.get(a), dto.getAnalogChannelValue().get(a), station));
                        }
                        realValueList.addAll(rs485RealList);
                        realValueList.addAll(analogRealList);

                        // 6.流量数据处理
                        flowDataDeal(realtimeData, station, realValueList, dto, interval);

                        // 7.降雨量数据处理
                        rainfallDataDeal(realtimeData, station, realValueList, dto, interval);

                        //8.更新实时数据
                        task.realtimeService.insertUpdateRealtime(realtimeData);

                        //9.添加状态记录
                        task.realtimeService.insertStateRecord(realtimeData);

                        //10.数据转发
                        if (ON.equals(station.getState())) {
                            dataForwarding(station, realtimeData, rs485RealList, analogRealList);
                        }

                        //11.检查是否触发浮标站下发服务
                        if (!realValueList.isEmpty()) {
                            writeToBuoyStation(station.getId(), realValueList);
                        }
                    }
                } catch (Exception e) {
                    log.error("实时记录异常[" + dto.getAreaCode() + "-" + dto.getAddress() + "]", e);
                }
            }
        }
    }

    private void dataForwarding(StationBean station, RealtimeDataBean realtimeData, List<Map<String, Object>> rs485RealList, List<Map<String, Object>> analogRealList) {
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

    private void dataPreprocessing(StationBean station, RealtimeDataBean realtimeData, RunningStateDto dto) {
        //缓存版本号
        redisUtil.hset("versionList", dto.getAreaCode() + dto.getAddress(), dto.getVersion());
        realtimeData.setStationId(station.getId());
        realtimeData.setLongitude(station.isGpsRead() ? dto.getLongitude() : station.getLongitude());
        realtimeData.setLatitude(station.isGpsRead() ? dto.getLatitude() : station.getLatitude());
        realtimeData.setNetwork("在线");
        realtimeData.setRunState(dto.getRunState());
        realtimeData.setFaultState(dto.getFaultState());
        if ("时间异常".equals(dto.getFaultState())) {
            task.redisUtil.hset(dto.getAreaCode() + dto.getAddress(), "timeSetting", ON);
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
        realtimeData.setOutletLevel(dto.getLevel());
        realtimeData.setSampleTime(CommonUtil.checkDate(dto.getSampleTime()) ? dto.getSampleTime() : "");
        //等待指令下发队列顺序下发
        ThreadPoolUtil.execute(() -> task.waitingQueueService.readySendQueue(dto.getCommunicationMode(), dto.getAreaCode(), dto.getAddress()));
    }

    private void levelCheck(StationBean station, RealtimeDataBean realtimeData, RunningStateDto dto) {
        //对开启“水深液位”的设备进行数据处理
        if (ON.equals(station.getDevice().getIsLevel())) {
            boolean onlySendSuperAdmin = false;
            boolean levelWarn = false;
            //FFFFFFFF传感器通讯异常
            if (WRONG_DATA.equals(dto.getLevel())) {
                realtimeData.setLevelState("故障");
                onlySendSuperAdmin = true;
                //数据读取异样
            } else if (Double.valueOf(dto.getLevel()).equals(-0.01)) {
                onlySendSuperAdmin = true;
                realtimeData.setLevelState("故障");
                double min = Double.parseDouble(station.getDevice().getLevelFakeMin());
                double max = Double.parseDouble(station.getDevice().getLevelFakeMax());
                //范围内随机生成数据
                realtimeData.setOutletLevel(CommonUtil.getRandomNumber(min, max));
                //正常值
            } else {
                realtimeData.setLevelState("正常");
                double warnValue = Double.parseDouble(station.getDevice().getLevelWarn());
                if (Double.valueOf(dto.getLevel()) > warnValue) {
                    levelWarn = true;
                }
            }
            if (ON.equals(station.getState()) && levelWarn) {
                //给该站点数据权限拥有者发送超标预警短信
                sendMessage(station, LEVEL_WARN, "originLevel", "液位超高预警，液位：" + dto.getLevel() + "m");
            }
            //给超级管理员发送状态异常短信
            if (onlySendSuperAdmin) {
                sendSuperAdminMessage(station);
            }
        }
    }

    private void sendSuperAdminMessage(StationBean station) {
        List<Map<String, Object>> superAdminList = companyService.getSuperAdminPhone(LEVEL_WARN);
        List<WarningMsg> sendList = getSendMsgList(superAdminList, station, LEVEL_WARN, LEVEL_WARN, "超声波液位测量(-0.01)");
        if (!sendList.isEmpty()) {
            sendMessageServer(sendList);
        }
    }

    private List<WarningMsg> getSendMsgList(List<Map<String, Object>> userList, StationBean station, String warnType,
                                            String keyWord, String content) {
        List<WarningMsg> sendList = new ArrayList<>();
        if (!userList.isEmpty()) {
            for (Map<String, Object> userMap : userList) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                boolean send = (hours < 22 && hours > 7) || Boolean.valueOf(userMap.get("night").toString());
                if (send) {
                    String key = station.getId() + "_" + userMap.get("id") + "_" + keyWord;
                    Object wait = redisUtil.hget(key, warnType);
                    if (wait == null) {
                        int interval = Integer.parseInt(userMap.get("interval").toString());
                        sendList.add(new WarningMsg(userMap.get("number").toString(), warnType, station, key, interval, content));
                    }
                }
            }
        }
        return sendList;
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加并反馈周期记录
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "0/10 * * * * ?")
    private void insertCycleRecord() {
        List<String> cycleList = new ArrayList<>();
        String info;
        do {
            info = redisUtil.lrGet("valueRecord");
            if (info != null) {
                cycleList.add(info);
            }
        } while (info != null);
        if (!cycleList.isEmpty()) {
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (String cycleStr : cycleList) {
                ValueRecordDto dto = JSON.parseObject(cycleStr, ValueRecordDto.class);
                try {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put(AREA_CODE, dto.getAreaCode());
                    map.put(ADDRESS, dto.getAddress());
                    StationBean station = task.stationService.getStationByMap(map);


                    List<Map<String, Object>> rs485MapList = (List<Map<String, Object>>) JSON.parse(company.get("rs485").toString());
                    //模拟通道因子
                    List<Map<String, Object>> analogMapList = (List<Map<String, Object>>) JSON.parse(company.get("analog").toString());
                    for (HistoryChildRecord record : dto.getValueList()) {
                        //上下位机因子配置如果不匹配，不更新因子数据
                        if (rs485MapList.size() != record.getR485ChannelValue().size()) {
                            continue;
                        }
                        Map<String, Object> runMap = new HashMap<>();
                        runMap.put("address", dto.getAddress());
                        runMap.put("areaCode", dto.getAreaCode());
                        runMap.put("sampleTime", record.getSampleTime());
                        runMap.put("outletLevel", record.getLevel());
                        //遍历rs485因子获取真实值
                        List<Map<String, Object>> rs485RealList = new ArrayList<>();
                        for (int a = 0; a < rs485MapList.size(); a++) {
                            Map<String, Object> rs485Map = rs485MapList.get(a);
                            String value = record.getR485ChannelValue().get(a);
                            if ("异常".equals(value)) {
                                rs485Map.put("value", "异常");
                            } else if (rs485Map.containsKey("special") && !"".equals(rs485Map.get("special").toString()) && Double.valueOf(value).equals(Double.valueOf(rs485Map.get("special").toString()))) {
                                rs485Map.put("value", "故障");
                            } else {
                                double realValue = Double.valueOf(value) + Double.valueOf(rs485Map.get("compensation").toString());
                                if (realValue < Double.valueOf(rs485Map.get("min").toString()) || realValue > Double.valueOf(rs485Map.get("max").toString())) {
                                    rs485Map.put("value", "故障");
                                } else {
                                    rs485Map.put("value", String.format("%.3f", realValue));
                                }
                            }
                            rs485RealList.add(rs485Map);
                        }

                        //遍历模拟通道因子获取真实值
                        List<Map<String, Object>> analogRealList = new ArrayList<>();
                        for (int a = 0; a < analogMapList.size(); a++) {
                            Map<String, Object> analogMap = analogMapList.get(a);
                            String value = record.getAnalogValue().get(a);
                            if ("异常".equals(value)) {
                                analogMap.put("value", "异常");
                            } else if (analogMap.containsKey("special") && !"".equals(analogMap.get("special").toString()) && Double.valueOf(value) == Double.valueOf(analogMap.get("special").toString())) {
                                analogMap.put("value", "故障");
                            } else {
                                double realValue = Double.valueOf(value) + Double.valueOf(analogMap.get("compensation").toString());
                                if (realValue < Double.valueOf(analogMap.get("min").toString()) || realValue > Double.valueOf(analogMap.get("max").toString())) {
                                    analogMap.put("value", "故障");
                                } else {
                                    analogMap.put("value", String.format("%.3f", realValue));
                                }
                            }
                            analogRealList.add(analogMap);
                        }
                        List<Map<String, Object>> realValueList = new ArrayList<>();
                        realValueList.addAll(rs485RealList);
                        realValueList.addAll(analogRealList);
                        runMap.put("valueList", realValueList.toString());
                        dataList.add(runMap);
                    }
                    task.operationRecordService.insertCycleRecordList(dataList);
                    ThreadPoolUtil.execute(() -> task.waitingQueueService.feedbackCycleUpload(dto.getCommunicationMode(), dto.getAreaCode(), dto.getAddress()));
                } catch (Exception e) {
                    log.error("周期记录异常[" + dto.getAreaCode() + "-" + dto.getAddress() + "]", e);
                }
            }
        }
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
        String time = DateUtil.parseDate2Str(new Date());
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        String beginTime = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":00:00";
        String endTime = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":59:59";
        String range = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":00" + " ~ " + (h + 1) + ":00";

        Map<String, Object> map = new HashMap<>(1);
        map.put(IS_FLOW, ON);
        List<StationBean> list = task.stationService.getStationListByMap(map);
        //原始数据List
        List<Map<String, Object>> dataList = new ArrayList<>();
        //保存数据List
        List<Map<String, Object>> saveRecordList = new ArrayList<>();
        for (StationBean station : list) {
            DeviceBean device = station.getDevice();
            String state = station.getState();
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("address", address);
                param.put("areaCode", areaCode);
                //获取最新的累积流量值
                Map<String, Object> maxFlowMap = task.operationRecordService.getMaxFlow(param);
                if (maxFlowMap != null) {
                    param.put("beginTime", beginTime);
                    param.put("endTime", endTime);
                    String maxFlow = maxFlowMap.get("total_flow").toString();
                    //获取上一小时的累积流量值
                    Object totalFlow = task.redisUtil.hget(areaCode + address, "hourTotalFlow");
                    String startFlowStr = totalFlow == null || totalFlow == "" ? "0.0" : totalFlow.toString();
                    Double endFlow = "".equals(maxFlow.trim()) ? 0.00 : Double.valueOf(maxFlow);
                    Double startFlow = "".equals(startFlowStr.trim()) ? 0.00 : Double.valueOf(startFlowStr);
                    Map<String, Object> hourFlowMap = new HashMap<>();
                    hourFlowMap.put("address", address);
                    hourFlowMap.put("areaCode", areaCode);
                    hourFlowMap.put("totalFlow", endFlow);
                    hourFlowMap.put("emissions", String.format("%.2f", endFlow - startFlow));
                    hourFlowMap.put("range", range);
                    Map<String, Object> avg = task.operationRecordService.getFlowAvg(param);
                    hourFlowMap.put("levelHeight", "0.000");
                    hourFlowMap.put("flow", "0.00");
                    hourFlowMap.put("speed", "0.000");
                    if (avg != null) {
                        if (avg.get("levelHeight") != null) {
                            hourFlowMap.put("levelHeight", String.format("%.3f", avg.get("levelHeight")));
                        }
                        if (avg.get("speed") != null) {
                            hourFlowMap.put("speed", String.format("%.3f", avg.get("speed")));
                        }
                        if (avg.get("flow") != null) {
                            hourFlowMap.put("flow", String.format("%.2f", avg.get("flow")));
                        }
                    }
                    if (Double.valueOf(hourFlowMap.get("emissions").toString()) < 0) {
                        hourFlowMap.put("emissions", "0.00");
                    }
                    hourFlowMap.put("time", time);
                    // 如果此时最新累积流量为异常值，则保存上一次的累计流量值，否则保存最新的累积流量
                    task.redisUtil.hset(areaCode + address, "hourTotalFlow", maxFlow);
                    dataList.add(hourFlowMap);
                }
            } catch (Exception e) {
                log.error("小时记录异常[" + areaCode + "-" + address + "]", e);
            }
        }
        if (!dataList.isEmpty()) {
            task.realtimeService.insertFlowHourRecordList(dataList);
        }
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加流量月统计（即日记录）数据1
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "00 55 23 * * ?")
    private void insertDayRecord1() {
        Map<String, Object> map = new HashMap<>();
        map.put("isFlow", 1);
        List<Map<String, Object>> list = companyService.getListByMap(map);
        for (Map<String, Object> one : list) {
            String address = String.valueOf(one.get("address"));
            String areaCode = String.valueOf(one.get("area_code"));
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("address", address);
                param.put("areaCode", areaCode);
                Map<String, Object> maxFlowMap = task.operationRecordService.getMaxFlow(param);
                if (maxFlowMap != null) {
                    String maxFlow = maxFlowMap.get("total_flow").toString();
                    task.redisUtil.hset(areaCode + address, "dayEndTotalFlow", maxFlow);
                }
            } catch (Exception e) {
                log.error("日记录异常[" + areaCode + "-" + address + "]", e);
            }
        }
    }

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加流量月统计（即日记录）数据2
     * @Date 13:15  2021/3/16
     * @Param
     **/
    @Scheduled(cron = "00 00 01 * * ?")
    private void insertDayRecord() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.add(Calendar.DATE, -1);
        int y = ca.get(Calendar.YEAR);
        int m = ca.get(Calendar.MONTH) + 1;
        int d = ca.get(Calendar.DATE);
        String day = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d);
        String beginTime = day + " " + "00:00:00";
        String endTime = day + " " + "23:59:59";
        Map<String, Object> map = new HashMap<>();
        map.put("isFlow", 1);
        List<Map<String, Object>> list = companyService.getListByMap(map);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("beginTime", beginTime);
        param.put("endTime", endTime);
        for (Map<String, Object> one : list) {
            String address = String.valueOf(one.get("address"));
            String areaCode = String.valueOf(one.get("area_code"));
            try {
                Object dayEndFlow = task.redisUtil.hget(areaCode + address, "dayEndTotalFlow");
                if (dayEndFlow != null) {
                    param.put("address", address);
                    param.put("areaCode", areaCode);
                    String maxFlow = dayEndFlow.toString();
                    Object totalFlow = task.redisUtil.hget(areaCode + address, "dayTotalFlow");
                    String startFlowStr = totalFlow == null ? "0.0" : totalFlow.toString();
                    Double endFlow = "".equals(maxFlow.trim()) ? 0.00 : Double.valueOf(maxFlow);
                    Double startFlow = "".equals(startFlowStr.trim()) ? 0.00 : Double.valueOf(startFlowStr);
                    Map<String, Object> dayFlowMap = new HashMap<>();
                    dayFlowMap.put("address", address);
                    dayFlowMap.put("areaCode", areaCode);
                    dayFlowMap.put("startFlow", startFlow);
                    dayFlowMap.put("totalFlow", endFlow);
                    dayFlowMap.put("emissions", String.format("%.2f", endFlow - Double.valueOf(startFlow)));
                    if (Double.valueOf(dayFlowMap.get("emissions").toString()) < 0) {
                        dayFlowMap.put("emissions", "0.00");
                    }
                    Map<String, Object> avg = task.operationRecordService.getFlowAvg(param);
                    dayFlowMap.put("levelHeight", "0.000");
                    dayFlowMap.put("flow", "0.00");
                    dayFlowMap.put("speed", "0.000");
                    if (avg != null) {
                        if (avg.get("levelHeight") != null) {
                            dayFlowMap.put("levelHeight", String.format("%.3f", avg.get("levelHeight")));
                        }
                        if (avg.get("flow") != null) {
                            dayFlowMap.put("flow", String.format("%.2f", avg.get("flow")));
                        }
                        if (avg.get("speed") != null) {
                            dayFlowMap.put("speed", String.format("%.3f", avg.get("speed")));
                        }
                    }
                    dayFlowMap.put("range", day);
                    dayFlowMap.put("time", endTime);
                    task.redisUtil.hset(areaCode + address, "dayTotalFlow", maxFlow);
                    dataList.add(dayFlowMap);
                }
            } catch (Exception e) {
                log.error("日记录异常[" + areaCode + "-" + address + "]", e);
            }
        }
        if (!dataList.isEmpty()) {
            task.realtimeService.insertFlowDayRecordList(dataList);
        }
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
                    String valueStr = map.get("value").toString();
                    Double value = ("异常".equals(valueStr) || "故障".equals(valueStr)) ? 0.0 : Double.valueOf(valueStr);
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


    private void rainfallDataDeal
            (RealtimeDataBean realtimeData, StationBean station, List<Map<String, Object>> realValueList, RunningStateDto
                    dto, int interval) {
        Map<String, Object> pulseBasicMap = realtimeService.getPulseBasic(station.getId());
        DeviceBean device = station.getDevice();
        realtimeData.setRainfallIntensity("");
        if (ON.equals(device.getIsRainfall())) {
            // 雨量脉冲值
            String rainfallPulseStr = isValid(realValueList, device.getRainfallPulseName(), dto);

            //雨量脉冲基础值
            double pulseBasic = 0.00;

            //时间间隔
            int min = interval;
            if (pulseBasicMap != null && pulseBasicMap.containsKey("pulse_base")) {
                String pulseBaseStr = pulseBasicMap.get("pulse_base").toString();
                pulseBasic = ("null".equals(pulseBaseStr) || pulseBaseStr.isEmpty()) ? 0.00 : Double.valueOf(pulseBaseStr);
            }
            min = min <= 0 ? 1 : min;
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
            realtimeData.setRainfall(String.format("%.3f", rainfall));
            realtimeData.setPulseBase(String.valueOf(originPulse));
            realtimeData.setPulseUpload(String.valueOf(pulse));
            realtimeData.setPulseCount(String.valueOf(originPulse));
            realtimeData.setRainfallIntensity(String.format("%.3f", intensity));
            realtimeData.setRainInterval(interval);
            task.realtimeService.insertRainfallMinRecord(realtimeData);
        }
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
            if (voltageWarn && task.redisUtil.hget(VOLTAGE_WARN, String.valueOf(station.getId())) != null
                    && OFF.equals(task.redisUtil.hget(VOLTAGE_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 设备内电压 ] 预警值：" + dto.getInternalVoltage()));
            }
            task.redisUtil.hset(VOLTAGE_WARN, String.valueOf(station.getId()), voltageWarn ? ON : OFF);

            boolean sensorVoltageWarn = ON.equals(station.getDevice().getIsSensor()) && (Double.valueOf(dto.getSensorVoltage()) < svMin || Double.valueOf(dto.getSensorVoltage()) > svMax);
            realtimeData.setSensorVoltageWarn(sensorVoltageWarn);
            if (sensorVoltageWarn && task.redisUtil.hget(SENSOR_VOLTAGE_WARN, String.valueOf(station.getId())) != null
                    && OFF.equals(task.redisUtil.hget(SENSOR_VOLTAGE_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 传感器电压 ] 预警值：" + dto.getSensorVoltage()));
            }
            task.redisUtil.hset(SENSOR_VOLTAGE_WARN, String.valueOf(station.getId()), sensorVoltageWarn ? ON : OFF);

            boolean humidityWarn = Double.valueOf(dto.getHumidity()) < hMin || Double.valueOf(dto.getHumidity()) > hMax;
            realtimeData.setHumidityWarn(humidityWarn);
            if (humidityWarn && task.redisUtil.hget(HUMIDITY_WARN, String.valueOf(station.getId())) != null
                    && OFF.equals(task.redisUtil.hget(HUMIDITY_WARN, String.valueOf(station.getId())).toString())) {
                task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), "设备预警", "[ 湿度 ] 预警值：" + dto.getHumidity()));
            }
            task.redisUtil.hset(HUMIDITY_WARN, String.valueOf(station.getId()), humidityWarn ? ON : OFF);
        }
    }

    /**
     * @param
     * @description 对数据进行预处理（异常值、补偿值），以及正常运行状态下的异常记录及短信报发，
     * @author Kohaku_川
     * @date 2021/10/27 9:57
     */
    private Map<String, Object> valueDeal(Map<String, Object> map, String
            value, StationBean station) {
        if (!WRONG_DATA.equals(value)) {
            //实际值=上传值+补偿值，保留三位小数
            double realValue = Double.valueOf(value) + Double.valueOf(map.get("compensation").toString());
            map.put("value", String.format("%.3f", realValue));
            if (ON.equals(station.getState())) {
                //1级预警
                double warn1Min = Double.parseDouble(map.get("warn1Min").toString());
                double warn1Max = Double.parseDouble(map.get("warn1Max").toString());
                if (realValue < warn1Min || realValue > warn1Max) {
                    task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), ONE_LEVEL_WARN_NAME, "[ " + map.get("name") + " ] 测量值：" + realValue));
                    sendMessage(station, ONE_LEVEL_WARN, map.get("name").toString(), "测量值:" + realValue);
                }
                //2级预警
                double warn2Min = Double.parseDouble(map.get("warn2Min").toString());
                double warn2Max = Double.parseDouble(map.get("warn2Max").toString());
                if (realValue < warn2Min || realValue > warn2Max) {
                    task.operationRecordService.insertExceptionRecord(new ExceptionRecordBean(station.getId(), TWO_LEVEL_WARN_NAME, "[ " + map.get("name") + " ] 测量值：" + realValue));
                    sendMessage(station, TWO_LEVEL_WARN, map.get("name").toString(), "测量值:" + realValue);
                }
            }
        }
        return map;
    }

    /**
     * @param
     * @description 流量数据处理
     * @author Kohaku_川
     * @date 2021/10/27 10:20
     */
    private void flowDataDeal
    (RealtimeDataBean realtimeData, StationBean station, List<Map<String, Object>> realValueList, RunningStateDto
            dto, int interval) {
        String flow = "";
        String speed = "0";
        String levelHeight = "0";
        String emissions = "";
        String totalFlow = "";
        DeviceBean device = station.getDevice();
        if (ON.equals(device.getIsFlow())) {
            // 液位高度
            levelHeight = isValid(realValueList, device.getLevelHeightName(), dto);

            // 流速
            speed = isValid(realValueList, device.getSpeedName(), dto);

            //累积流量基础值
            double totalFlowBasic = 0.00;
            Map<String, Object> flowBasicMap = realtimeService.getTotalFlowBasic(station.getId());

            //时间间隔
            int min = interval;
            if (flowBasicMap != null) {
                String baseFlowStr = String.valueOf(flowBasicMap.get("flow_base"));
                totalFlowBasic = ("null".equals(baseFlowStr) || "异常".equals(baseFlowStr) || baseFlowStr.isEmpty()) ? 0.00 : Double.valueOf(baseFlowStr);
            }

            // 原始值上传
            if ("0".equals(device.getFlowCount())) {
                // 瞬时流量
                flow = isValid(realValueList, device.getFlowName(), dto);

                //累计流量
                String totalFlowName = device.getTotalFlowName();
                for (Map<String, Object> map : realValueList) {
                    if (map.containsValue(totalFlowName)) {
                        totalFlow = map.get("value").toString();
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
                double flowValue = 0;
                double emissionsValue = 0;
                double totalFlowValue = 0;
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
                        emissionsValue = flowValue * min / 60;
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
                        emissionsValue = flowValue * min / 60;
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
                        emissionsValue = flowValue * min / 60;
                        emissions = String.format("%.2f", emissionsValue);
                        //累计流量
                        totalFlowValue = totalFlowBasic + emissionsValue;
                        totalFlow = String.format("%.2f", totalFlowValue);
                        break;
                    default:
                        break;
                }
            }
        }
        realtimeData.setValueList(realValueList.toString());
        realtimeData.setFlow(flow);
        realtimeData.setSpeed(speed);
        realtimeData.setLevelHeight(levelHeight);
        realtimeData.setEmissions(emissions);
        realtimeData.setRainInterval(interval);
        if (!"".equals(emissions) && Double.valueOf(emissions) < 0) {
            realtimeData.setEmissions("0.0");
        }
        realtimeData.setTotalFlow(totalFlow);
        if (CommonUtil.isDouble(totalFlow)) {
            task.realtimeService.updateFlowBase(station.getId(), totalFlow);
        }
    }

    private String isValid(List<Map<String, Object>> list, String name, RunningStateDto dto) {
        String value = "0.00";
        switch (name) {
            case "无":
                value = "--";
                break;
            case "水深液位":
                value = ("异常".equals(dto.getLevel()) || "".equals(dto.getLevel())) ? "0.00" : dto.getLevel();
                break;
            default:
                for (Map<String, Object> map : list) {
                    if (map.containsValue(name)) {
                        value = map.get("value").toString();
                        value = ("异常".equals(value) || "".equals(value)) ? "0.00" : value;
                        list.remove(map);
                        break;
                    }
                }
                break;
        }
        return value;
    }

    private void sendMessage(StationBean station, String warnType, String keyWord, String content) {
        if (!DEV_MODE.equals(active)) {
            List<Map<String, Object>> superAdminList = companyService.getSuperAdminPhone(warnType);
            List<Map<String, Object>> normalUserList = companyService.getNormalUserPhone(station.getBlockUrl(), warnType);
            List<Map<String, Object>> userList = new ArrayList<>();
            userList.addAll(superAdminList);
            userList.addAll(normalUserList);
            List<WarningMsg> sendList = getSendMsgList(userList, station, warnType, keyWord, content);
            if (!sendList.isEmpty()) {
                sendMessageServer(sendList);
            }
        }
    }

    private void sendMessageServer(List<WarningMsg> sendList) {
        for (WarningMsg warningMsg : sendList) {
            StationBean station = warningMsg.getStation();
            String[] numbers = {warningMsg.getNumber()};
            String[] params = {station.getName(), warningMsg.getWarnType(), warningMsg.getContent()};
            SmsMultiSender sender = new SmsMultiSender(1400293649, "82e02452bc16d9b7992568388fad642d");
            SmsMultiSenderResult result = null;
            try {
                result = sender.sendWithParam("86", numbers,
                        1385584, params, "小桥流水环境科技", "", "");
            } catch (HTTPException | IOException | com.github.qcloudsms.httpclient.HTTPException e) {
                e.printStackTrace();
            }
            Map<String, Object> recordMap = new HashMap<>(4);
            recordMap.put("content", "【" + station.getName() + "】站点，" + warningMsg.getWarnType() + "，" + warningMsg.getContent() + "，请关注该站点的数据变化！");
            recordMap.put("stationId", station.getId());
            recordMap.put("type", warningMsg.getKey());
            if (Objects.requireNonNull(result).result == 0) {
                recordMap.put("feedback", "发送成功");
                redisUtil.hset(warningMsg.getKey(), warningMsg.getWarnType(), "1", warningMsg.getInterval() * 60 * 60);
            } else {
                recordMap.put("feedback", "错误码：" + result.result);
            }
            task.operationRecordService.insertMessageRecord(recordMap);
        }
    }

}
