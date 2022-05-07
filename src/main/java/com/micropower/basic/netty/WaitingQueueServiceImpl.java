package com.micropower.basic.netty;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.common.dto.receive.*;
import com.micropower.basic.common.dto.send.*;
import com.micropower.basic.netty.syncWrite.SyncWriteFuture;
import com.micropower.basic.netty.syncWrite.SyncWriteMap;
import com.micropower.basic.netty.syncWrite.WriteFuture;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.util.DateUtil;
import com.micropower.basic.util.RedisUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2021/3/16 16:01
 * @Description: TODO → 顺序进行指令下发/接收回复
 * @Author:Kohaku_川
 **/
@Service
@Slf4j
@Component
public class WaitingQueueServiceImpl implements WaitingQueueService {

    private static WaitingQueueServiceImpl service;

    private static final String NULL = "null";
    private static final String UDP = "udp";
    private static final String TCP = "tcp";
    private static final String SUCCESS = "feedback";
    private static final String DATA = "data";
    private static final String SWITCHOVER_WORK_STATE = "switchoverSetting";
    private static final String CHECK_CHANNEL_SETTING = "checkChannelSetting";
    private static final String MODEL_SETTING = "modelSetting";
    private static final String LEVEL_SETTING = "levelSetting";
    private static final String WATER_FACTOR_SETTING = "waterFactorSetting";
    private static final String NETWORK_SETTING = "networkSetting";
    private static final String SERIAL_PORT_SETTING = "serialPortSetting";
    private static final String POWER_CONTROL_SETTING = "powerControlSetting";
    private static final String TIME_SETTING = "timeSetting";
    private static final String QUERY_OPERATION = "queryOperationRecord";
    private static final String QUERY_EXCEPTION = "queryExceptionRecord";
    private static final String QUERY_HISTORY = "queryHistoryRecord";
    private static final String QUERY_ALL_SETTING = "queryAllSetting";
    private static final String AREA_ADDRESS_SETTING = "areaAddressSetting";
    private static final String BUILT_IN_LEVEL_SENSOR_SETTING = "builtInLevelSensorSetting";
    private static final String UPGRADE_DEVICE = "upgradeDevice";
    private static final String UPGRADE_DEVICE_2 = "upgradeDevice2";
    private static final String SERIAL_PORT_PROTOCOL_SETTING = "serialPortProtocolSetting";
    private static final String MOD_BUS_PASS_THROUGH_CONTROL_SETTING = "modBusPassThroughControlSetting";
    private static final String CHANNEL_POWER_ON_DELAY_SETTING = "channelPowerOnDelaySetting";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OperationRecordService operationRecordService;

    @PostConstruct
    public void init() {
        service = this;
        service.redisUtil = this.redisUtil;
        service.operationRecordService = this.operationRecordService;
    }

    @Override
    public void readySendQueue(String communicationMode, String areaCode, Integer address) {
        try {
            //设置液位测量辅助参数
            boolean send1 = levelSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //设置工作模式及相关参数
            boolean send2 = modelSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //设置网络参数
            boolean send3 = networkSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //扩展通道开/关（功耗控制）
            boolean send4 = powerControlSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //设置RS485串口参数
            boolean send5 = serialPortSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //时间校准
            boolean send6 = timeCalibrationSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //水质因子参数设置
            boolean send7 = waterFactorSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //RS485串口协议配置
            boolean send8 = serialPortProtocolSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //MODBUS透传控制
            boolean send9 = modBusPassThroughControlSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //4-20模拟通道起电延时设置
            boolean send10 = channelPowerOnDelaySettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //4-20模拟通道校零
            boolean send16 = CheckChannelSettingSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //设置内置液位传感器类型
            boolean send11 = builtInLevelSensorSetting(communicationMode, areaCode, address);
            Thread.sleep(500);
            //切换设备工作状态
            boolean send12 = switchoverWorkStateSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //查询操作日志
//            queryOperationRecord(communicationMode, areaCode, address);
//            Thread.sleep(500);
            //查询异常日志
//            queryExceptionRecord(communicationMode, areaCode, address);
//            Thread.sleep(500);
            //查询历史记录
//            queryHistoryRecord(communicationMode, areaCode, address);
//            Thread.sleep(500);
            //固件升级（老版本）
            boolean send13 = upgradeDeviceSend(communicationMode, areaCode, address);
            Thread.sleep(500);
            //固件升级（新版本）
            boolean send14 = upgradeDeviceSend2(communicationMode, areaCode, address);
            Thread.sleep(500);
            //设置终端逻辑地址
            boolean send15 = areaAddressSetting(communicationMode, areaCode, address);
            Thread.sleep(500);
            //查询参数
            boolean sendQuery = false;
            if (send1 || send2 || send3 || send4 || send5 || send6 || send7 || send8 || send9 || send10 || send11 || send12 || send13 || send14 || send15) {
                sendQuery = true;
            }
            queryAllSetting(communicationMode, areaCode, address, sendQuery);
            Thread.sleep(500);
            //结束通讯
            endCommunication(communicationMode, areaCode, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void feedbackCycleUpload(String communicationMode, String areaCode, Integer address) {
        CycleFeedbackDto dto = new CycleFeedbackDto();
        dto.setAreaCode(areaCode);
        dto.setAddress(address);
        sendControl(true, dto, communicationMode);
    }

    private boolean builtInLevelSensorSetting(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, BUILT_IN_LEVEL_SENSOR_SETTING));
        if (!NULL.equals(info)) {
            BuiltInLevelSensorSettingDto dto = JSONObject.parseObject(info, BuiltInLevelSensorSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, BUILT_IN_LEVEL_SENSOR_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, BUILT_IN_LEVEL_SENSOR_SETTING);
            return true;
        }
        return false;
    }

    private boolean areaAddressSetting(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, AREA_ADDRESS_SETTING));
        if (!NULL.equals(info)) {
            AreaAddressSettingDto dto = JSONObject.parseObject(info, AreaAddressSettingDto.class);
            sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, AREA_ADDRESS_SETTING, redisUtil.hget(dto.getAreaNo() + dto.getDeviceNo(), "address_set_success") != null);
            redisUtil.hdel(areaCode + address, AREA_ADDRESS_SETTING);
            return true;
        }
        return false;
    }

    private boolean channelPowerOnDelaySettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, CHANNEL_POWER_ON_DELAY_SETTING));
        if (!NULL.equals(info)) {
            ChannelPowerOnDelaySettingDto dto = JSONObject.parseObject(info, ChannelPowerOnDelaySettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, CHANNEL_POWER_ON_DELAY_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, CHANNEL_POWER_ON_DELAY_SETTING);
            return true;
        }
        return false;
    }

    private boolean CheckChannelSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, CHECK_CHANNEL_SETTING));
        if (!NULL.equals(info)) {
            CheckChannelDto dto = JSONObject.parseObject(info, CheckChannelDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, CHECK_CHANNEL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, CHECK_CHANNEL_SETTING);
            return true;
        }
        return false;
    }

    private boolean modBusPassThroughControlSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, MOD_BUS_PASS_THROUGH_CONTROL_SETTING));
        if (!NULL.equals(info)) {
            ModBusPassThroughControlSettingDto dto = JSONObject.parseObject(info, ModBusPassThroughControlSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, MOD_BUS_PASS_THROUGH_CONTROL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, MOD_BUS_PASS_THROUGH_CONTROL_SETTING);
            return true;
        }
        return false;
    }

    private boolean serialPortProtocolSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, SERIAL_PORT_PROTOCOL_SETTING));
        if (!NULL.equals(info)) {
            SerialPortProtocolSettingDto dto = JSONObject.parseObject(info, SerialPortProtocolSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, SERIAL_PORT_PROTOCOL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, SERIAL_PORT_PROTOCOL_SETTING);
            return true;
        }
        return false;
    }

    private boolean upgradeDeviceSend2(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, UPGRADE_DEVICE_2));
        if (!NULL.equals(info)) {
            UpgradeDeviceDto2 dto = JSONObject.parseObject(info, UpgradeDeviceDto2.class);
            int softwareVersion = Integer.valueOf(dto.getSoftwareVersionNo().substring(0, 1));
            String hardwareVersion = dto.getHardwareVersionNo();
            QuerySettingDto3 querySettingDto3 = new QuerySettingDto3();
            querySettingDto3.setAddress(address);
            querySettingDto3.setAreaCode(areaCode);
            Map<String, Object> queryVersionResult = sendControl(true, querySettingDto3, communicationMode);
            String checkHardVersion = "";
            boolean send = false;
            if (queryVersionResult.containsKey(DATA)) {
                QuerySettingBackDto data = (QuerySettingBackDto) queryVersionResult.get(DATA);
                checkHardVersion = data.getHardwareVersion();
                if (softwareVersion % 2 != 0 && "udp".equals(communicationMode) || softwareVersion % 2 == 0 && "tcp".equals(communicationMode)
                        || !checkHardVersion.equals(hardwareVersion)) {
                    redisUtil.hdel(areaCode + address, UPGRADE_DEVICE_2);
                    saveOperationRecord(areaCode, address, UPGRADE_DEVICE_2, false);
                    return false;
                }
                List<String> sendContent = dto.getPackageContent();
                int packageNumber = sendContent.size();
                QueryUpgradeVersion queryUpgradeVersion = new QueryUpgradeVersion();
                queryUpgradeVersion.setVersionNo(Integer.valueOf(dto.getSoftwareVersionNo()));
                queryUpgradeVersion.setTotalPackage(packageNumber);
                queryUpgradeVersion.setAreaCode(dto.getAreaCode());
                queryUpgradeVersion.setAddress(dto.getAddress());
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>【续传升级 - 询问文件传输包】<<<<<<<<<<<<<<<<<<<<<<<<<");
                Map<String, Object> queryBack = sendControl(true, queryUpgradeVersion, communicationMode);
                int i = 0;
                String finished;
                if (Boolean.valueOf(String.valueOf(queryBack.get(SUCCESS)))) {
                    QueryUpgradeVersionBack back = (QueryUpgradeVersionBack) queryBack.get(DATA);
                    if (back.getVersionNo().equals(Integer.valueOf(dto.getSoftwareVersionNo())) && back.getPackageNo() != packageNumber) {
                        i = back.getPackageNo();
                    }
                    int c = 0;
                    int d = 0;
                    int a = 0;
                    while (c < 1 && d < 2 && a < 2 && i < packageNumber) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        UpgradeDeviceDto2 onePackage = new UpgradeDeviceDto2();
                        onePackage.setSoftwareVersionNo(dto.getSoftwareVersionNo());
                        onePackage.setAddress(dto.getAddress());
                        onePackage.setAreaCode(dto.getAreaCode());
                        onePackage.setContent(sendContent.get(i));
                        onePackage.setPackageNo(i);
                        onePackage.setFrameLength(sendContent.get(i).length() / 2 + 17);
                        onePackage.setPackageBytes(sendContent.get(i).length() / 2);
                        onePackage.setTotalPackage(packageNumber);
                        finished = (i + 1) + " / " + packageNumber;
                        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>【续传升级 - " + finished + "】<<<<<<<<<<<<<<<<<<<<<<<<<");
                        Map<String, Object> result = sendControl(true, onePackage, communicationMode);
                        if (!Boolean.valueOf(String.valueOf(result.get(SUCCESS)))) {
                            Map<String, Object> queryBack1 = sendControl(true, queryUpgradeVersion, communicationMode);
                            if (Boolean.valueOf(String.valueOf(queryBack1.get(SUCCESS)))) {
                                QueryUpgradeVersionBack back1 = (QueryUpgradeVersionBack) queryBack1.get(DATA);
                                if (back1 == null) {
                                    send = false;
                                } else {
                                    i = back1.getPackageNo() - 1;
                                    send = true;
                                }
                            } else {
                                send = false;
                            }
                        } else {
                            send = true;
                        }
                        if (i == packageNumber - 1) {
                            d++;
                            if (send) {
                                c++;
                            } else {
                                i = 0;
                            }
                        } else {
                            if (!send) {
                                a++;
                            } else {
                                i++;
                            }
                        }
                    }
                } else {
                    send = false;
                }
            }
            if (send) {
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>【续传升级 - 升级成功】<<<<<<<<<<<<<<<<<<<<<<<<<");
                redisUtil.hdel(areaCode + address, UPGRADE_DEVICE_2);
                saveOperationRecord(areaCode, address, UPGRADE_DEVICE_2, true);
            } else {
                int times = dto.getTimes() + 1;
                if (times > 10) {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>【续传升级 - 升级失败】<<<<<<<<<<<<<<<<<<<<<<<<<");
                    redisUtil.hdel(areaCode + address, UPGRADE_DEVICE_2);
                    saveOperationRecord(areaCode, address, UPGRADE_DEVICE_2, false);
                } else {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>【续传升级 - 已升级" + times + "次】<<<<<<<<<<<<<<<<<<<<<<<<<");
                    dto.setTimes(times);
                    redisUtil.hset(areaCode + address, UPGRADE_DEVICE_2, JSONObject.toJSONString(dto, SerializerFeature.IgnoreErrorGetter));
                }
            }
            return true;
        }
        return false;
    }

    private void endCommunication(String communicationMode, String areaCode, Integer address) {
        EndCommunication dto = new EndCommunication();
        dto.setAddress(address);
        dto.setAreaCode(areaCode);
        sendControl(false, dto, communicationMode);
    }

    private boolean upgradeDeviceSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, UPGRADE_DEVICE));
        if (!NULL.equals(info)) {
            UpgradeDeviceDto dto = JSONObject.parseObject(info, UpgradeDeviceDto.class);
            List<String> sendContent = dto.getPackageContent();
            int packageNumber = sendContent.size();
            boolean send = true;
            int c = 0;
            int d = 0;
            int i = 0;
            while (c < 1 && d < 2 && i < packageNumber) {
                UpgradeDeviceDto onePackage = new UpgradeDeviceDto();
                onePackage.setAddress(dto.getAddress());
                onePackage.setAreaCode(dto.getAreaCode());
                onePackage.setContent(sendContent.get(i));
                onePackage.setPackageNo(i);
                onePackage.setFrameLength(sendContent.get(i).length() / 2 + 15);
                onePackage.setPackageBytes(sendContent.get(i).length() / 2);
                onePackage.setTotalPackage(packageNumber);
                Map<String, Object> result = sendControl(true, onePackage, communicationMode);
                if (!Boolean.valueOf(String.valueOf(result.get(SUCCESS)))) {
                    Map<String, Object> result2 = sendControl(true, onePackage, communicationMode);
                    if (!Boolean.valueOf(String.valueOf(result2.get(SUCCESS)))) {
                        Map<String, Object> result3 = sendControl(true, onePackage, communicationMode);
                        if (!Boolean.valueOf(String.valueOf(result3.get(SUCCESS)))) {
                            send = false;
                        }
                    }
                }
                if (i == packageNumber - 1) {
                    d++;
                    if (send) {
                        c++;
                    } else {
                        i = 0;
                    }
                } else {
                    if (!send) {
                        c++;
                    } else {
                        i++;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            saveOperationRecord(areaCode, address, UPGRADE_DEVICE, send);
            redisUtil.hdel(areaCode + address, UPGRADE_DEVICE);
            return true;
        }
        return false;
    }

    private void queryOperationRecord(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, QUERY_OPERATION));
        if (!NULL.equals(info)) {
            QueryOperationRecordDto dto = JSONObject.parseObject(info, QueryOperationRecordDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            while (result.containsKey(DATA)) {
                OperationRecordDto data = (OperationRecordDto) result.get(DATA);
                operationRecordService.insertDeviceOperationRecordList(data.getOperationChildRecordList());
                if (data.getTotalRecordNumber() != data.getRecordNumber()) {
                    QueryOperationRecordDto nextQuery = new QueryOperationRecordDto();
                    try {
                        Date timeDate = DateUtil.parseStr2Date(data.getEndTime());
                        nextQuery.setBeginTime(DateUtil.parseStr2Date(getTimeByMinute(timeDate, 1)));
                        nextQuery.setEndTime(dto.getEndTime());
                        nextQuery.setAddress(dto.getAddress());
                        nextQuery.setArea(dto.getArea());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    result = sendControl(true, nextQuery, communicationMode);
                } else {
                    break;
                }
            }
            saveOperationRecord(areaCode, address, QUERY_OPERATION, result.containsKey(DATA));
            redisUtil.hdel(areaCode + address, QUERY_OPERATION);
        }
    }

    private void queryExceptionRecord(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, QUERY_EXCEPTION));
        if (!NULL.equals(info)) {
            QueryExceptionRecordDto dto = JSONObject.parseObject(info, QueryExceptionRecordDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            while (result.containsKey(DATA)) {
                ExceptionRecordDto data = (ExceptionRecordDto) result.get(DATA);
                operationRecordService.insertExceptionRecordList(data.getExceptionChildRecords());
                if (data.getTotalRecordNumber() != data.getRecordNumber()) {
                    QueryExceptionRecordDto nextQuery = new QueryExceptionRecordDto();
                    try {
                        Date timeDate = DateUtil.parseStr2Date(data.getEndTime());
                        nextQuery.setBeginTime(DateUtil.parseStr2Date(getTimeByMinute(timeDate, 1)));
                        nextQuery.setEndTime(dto.getEndTime());
                        nextQuery.setAddress(dto.getAddress());
                        nextQuery.setArea(dto.getArea());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    result = sendControl(true, nextQuery, communicationMode);
                }
            }
            saveOperationRecord(areaCode, address, QUERY_EXCEPTION, result.containsKey(DATA));
            redisUtil.hdel(areaCode + address, QUERY_EXCEPTION);
        }
    }

    private void queryHistoryRecord(String communicationMode, String areaCode, Integer address) {
//        String info = String.valueOf(redisUtil.hget(areaCode + address, QUERY_HISTORY));
//        if (!NULL.equals(info)) {
//            QueryHistoryRecordDto dto = JSONObject.parseObject(info, QueryHistoryRecordDto.class);
//            Map<String, Object> result = sendControl(true, dto, communicationMode);
//            while (result.containsKey(DATA)) {
//                HistoryRecordDto data = (HistoryRecordDto) result.get(DATA);
//                if (data.getHistoryChildRecordList().size() > 0) {
//                    operationRecordService.insertHistoryRecordList(data.getHistoryChildRecordList());
//                }
//                if (data.getTotalRecordNumber() != data.getRecordNumber()) {
//                    QueryHistoryRecordDto nextQuery = new QueryHistoryRecordDto();
//                    try {
//                        Date timeDate = simpleDateFormat.parse(data.getLastTime());
//                        nextQuery.setBeginTime(simpleDateFormat.parse(getTimeByMinute(timeDate, 1)));
//                        nextQuery.setEndTime(dto.getEndTime());
//                        nextQuery.setAddress(dto.getAddress());
//                        nextQuery.setArea(dto.getArea());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    result = sendControl(true, nextQuery, communicationMode);
//                }
//            }
//            saveOperationRecord(areaCode, address, QUERY_HISTORY, result.containsKey(DATA));
//            redisUtil.hdel(areaCode + address, QUERY_HISTORY);
//        }
    }

    private void queryAllSetting(String communicationMode, String areaCode, Integer address, boolean query) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, QUERY_ALL_SETTING));
        if (!NULL.equals(info)) {
            QuerySettingDto dto = new QuerySettingDto();
            dto.setAddress(address);
            dto.setAreaCode(areaCode);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            boolean feedback = false;
            if (result.containsKey(DATA)) {
                QuerySettingBackDto data = (QuerySettingBackDto) result.get(DATA);
                QuerySettingDto2 dto2 = new QuerySettingDto2();
                dto2.setAddress(address);
                dto2.setAreaCode(areaCode);
                Map<String, Object> result2 = sendControl(true, dto2, communicationMode);
                if (result2.containsKey(DATA)) {
                    QuerySettingBackDto data2 = (QuerySettingBackDto) result2.get(DATA);
                    data.setIMEI(data2.getIMEI());
                    data.setCCID(data2.getCCID());
                    feedback = operationRecordService.insertUpdateDeviceConfig(data);
                }
            }
            saveOperationRecord(areaCode, address, QUERY_ALL_SETTING, feedback);
            redisUtil.hdel(areaCode + address, QUERY_ALL_SETTING);
        } else if (query) {
            QuerySettingDto dto = new QuerySettingDto();
            dto.setAddress(address);
            dto.setAreaCode(areaCode);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            if (result.containsKey(DATA)) {
                QuerySettingBackDto data = (QuerySettingBackDto) result.get(DATA);
                QuerySettingDto2 dto2 = new QuerySettingDto2();
                dto2.setAddress(address);
                dto2.setAreaCode(areaCode);
                Map<String, Object> result2 = sendControl(true, dto2, communicationMode);
                if (result2.containsKey(DATA)) {
                    QuerySettingBackDto data2 = (QuerySettingBackDto) result2.get(DATA);
                    data.setIMEI(data2.getIMEI());
                    data.setCCID(data2.getCCID());
                    operationRecordService.insertUpdateDeviceConfig(data);
                }
            }
        }
    }

    private boolean levelSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, LEVEL_SETTING));
        if (!NULL.equals(info)) {
            LevelSettingDto dto = JSONObject.parseObject(info, LevelSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, LEVEL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, LEVEL_SETTING);
            return true;
        }
        return false;
    }

    private boolean modelSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, MODEL_SETTING));
        if (!NULL.equals(info)) {
            ModelSettingDto dto = JSONObject.parseObject(info, ModelSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, MODEL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, MODEL_SETTING);
            return true;
        }
        return false;
    }

    private boolean networkSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, NETWORK_SETTING));
        if (!NULL.equals(info)) {
            NetworkSettingDto dto = JSONObject.parseObject(info, NetworkSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, NETWORK_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, NETWORK_SETTING);
            return true;
        }
        return false;
    }

    private boolean powerControlSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, POWER_CONTROL_SETTING));
        if (!NULL.equals(info)) {
            PowerControlDto dto = JSONObject.parseObject(info, PowerControlDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, POWER_CONTROL_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, POWER_CONTROL_SETTING);
            return true;
        }
        return false;
    }

    private boolean serialPortSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, SERIAL_PORT_SETTING));
        if (!NULL.equals(info)) {
            SerialPortSettingDto dto = JSONObject.parseObject(info, SerialPortSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, SERIAL_PORT_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, SERIAL_PORT_SETTING);
            return true;
        }
        return false;
    }

    private boolean switchoverWorkStateSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, SWITCHOVER_WORK_STATE));
        if (!NULL.equals(info)) {
            SwitchoverWorkStateDto dto = JSONObject.parseObject(info, SwitchoverWorkStateDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, SWITCHOVER_WORK_STATE, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, SWITCHOVER_WORK_STATE);
            return true;
        }
        return false;
    }

    private boolean timeCalibrationSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, TIME_SETTING));
        if (!NULL.equals(info)) {
            TimeCalibrationDto dto = new TimeCalibrationDto();
            dto.setAreaCode(areaCode);
            dto.setAddress(address);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, TIME_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, TIME_SETTING);
            return true;
        }
        return false;
    }

    private boolean waterFactorSettingSend(String communicationMode, String areaCode, Integer address) {
        String info = String.valueOf(redisUtil.hget(areaCode + address, WATER_FACTOR_SETTING));
        if (!NULL.equals(info)) {
            WaterFactorSettingDto dto = JSONObject.parseObject(info, WaterFactorSettingDto.class);
            Map<String, Object> result = sendControl(true, dto, communicationMode);
            saveOperationRecord(areaCode, address, WATER_FACTOR_SETTING, Boolean.valueOf(String.valueOf(result.get(SUCCESS))));
            redisUtil.hdel(areaCode + address, WATER_FACTOR_SETTING);
            return true;
        }
        return false;
    }

    private Map<String, Object> sendControl(boolean isWait, CommonDto commonDto, String communicationMode) {
        Map<String, Object> result = new HashMap<>();
        int waitTime = 20000;
        ChannelFuture future = null;
        final WriteFuture<CommonDto> writeFuture = new SyncWriteFuture(commonDto.getAreaCode() + commonDto.getAddress());
        SyncWriteMap.writeRecords.put(commonDto.getAreaCode() + commonDto.getAddress(), writeFuture);
        try {
            if (UDP.equalsIgnoreCase(communicationMode)) {
                DatagramPacket packet = ChannelCache.getPacket(commonDto.getAreaCode() + commonDto.getAddress());
                ChannelHandlerContext ctx = ChannelCache.getContext(commonDto.getAreaCode() + commonDto.getAddress());
                future = ctx.writeAndFlush(new DatagramPacket(commonDto.getEncode(Unpooled.buffer(200)), packet.sender()));
            } else if (TCP.equalsIgnoreCase(communicationMode)) {
                waitTime = 15000;
                Channel channel = ChannelCache.getChannel(commonDto.getAreaCode() + commonDto.getAddress());
                future = channel.writeAndFlush(commonDto);
            }
            if (future != null) {
                send(writeFuture, future);
            }
            result.put(SUCCESS, true);
            if (isWait) {
                CommonDto response;
                response = writeFuture.get(waitTime, TimeUnit.MILLISECONDS);
                SyncWriteMap.writeRecords.remove(writeFuture.requestId());
                if (response == null) {
                    result.put(SUCCESS, false);
                } else {
                    if (Arrays.asList(CommonDto.getQueryBackCode()).contains((response.getCode()))) {
                        result.put(DATA, response);
                    }
                    if (!response.isSuccess()) {
                        result.put(SUCCESS, false);
                    }
                }
            }
        } catch (Exception e) {
            log.error("通讯异常", e);
        }
        return result;
    }

    private void send(final WriteFuture<CommonDto> writeFuture, ChannelFuture future) {
        future.addListener((ChannelFutureListener) future1 -> {
            writeFuture.setWriteResult(future1.isSuccess());
            writeFuture.setCause(future1.cause());
            if (!writeFuture.isWriteSuccess()) {
                SyncWriteMap.writeRecords.remove(writeFuture.requestId());
            }
        });
    }

    private static String getTimeByMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    private void saveOperationRecord(String areaCode, int address, String name, boolean feedback) {
        String id = String.valueOf(redisUtil.hget(areaCode + address, name + "Id"));
        if (!"null".equalsIgnoreCase(id)) {
            operationRecordService.updateOperationRecord(id, feedback ? "执行成功" : "执行失败");
        }
    }
}
