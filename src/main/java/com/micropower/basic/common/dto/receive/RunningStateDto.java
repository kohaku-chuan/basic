package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DateUtil;
import com.micropower.basic.util.DecoderUtil;
import com.micropower.basic.util.StaticFinalWard;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Date: 2020/7/9 11:18
 * @Description: TODO →运行参数报文
 * @Author:Kohaku_川
 **/
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class RunningStateDto extends CommonDto {
    /**
     * 运行状态
     */
    String runState;

    /**
     * 故障状态
     */
    String faultState;

    /**
     * 实时RTC值
     */
    String realTimeClock;

    /**
     * 设备内电压
     */
    String internalVoltage;

    /**
     * 设备外电压
     */
    String externalVoltage;

    /**
     * 传感器供电电压
     */
    String sensorVoltage;

    /**
     * 内部温度
     */
    String temp;

    /**
     * 内部湿度
     */
    String humidity;

    /**
     * 安装倾斜角度
     */
    String tiltAngle;

    /**
     * RSSI
     */
    String rssi;

    /**
     * RSRP
     */
    String rsrp;

    /**
     * 纬度
     */
    String latitude;

    /**
     * 经度
     */
    String longitude;

    /**
     * 排放口液位
     */
    String level;

    /**
     * RS485通道变量个数
     */
    Integer r485ChannelNum;

    /**
     * RS485通道变量值
     */
    List<String> r485ChannelValue;

    /**
     * 模拟通道变量值
     */
    List<String> analogChannelValue;

    /**
     * 数据采集时间戳
     */
    String sampleTime;

    /**
     * 校验码
     */
    String checkCode;

    @Override
    protected void decode(String in, CommonDto commonDto) {
        try {
            RunningStateDto runningStateDto = (RunningStateDto) commonDto;
            DecimalFormat df = new DecimalFormat("0.00");
            //1字节-运行状态
            String run = in.substring(0, 2);
            switch (run) {
                case "00":
                    runningStateDto.setRunState("待机");
                    break;
                case "01":
                    runningStateDto.setRunState("运行");
                    break;
                case "02":
                    runningStateDto.setRunState("维护");
                    break;
                case "03":
                    runningStateDto.setRunState("检修");
                    break;
                default:
                    break;
            }
            //1字节-故障状态码
            String fault = in.substring(2, 4);
            runningStateDto.setFaultState("00".equals(fault) ? "正常" : "故障代码" + fault);
            //6字节-实时RTC值
            runningStateDto.setRealTimeClock(DecoderUtil.parseDate(in.substring(4, 16)));
            //2字节-设备内电压
            runningStateDto.setInternalVoltage(df.format((float) Integer.valueOf(in.substring(16, 20), 16) / 100));
            //2字节-设备外电压
            runningStateDto.setExternalVoltage(df.format((float) Integer.valueOf(in.substring(20, 24), 16) / 100));
            //2字节-传感器供电电压
            runningStateDto.setSensorVoltage(df.format((float) Integer.valueOf(in.substring(24, 28), 16) / 100));
            //2字节-温度
            runningStateDto.setTemp(String.valueOf(DecoderUtil.dwordBytesToLong(in.substring(28, 32))));
            //2字节-湿度
            runningStateDto.setHumidity(String.valueOf(DecoderUtil.dwordBytesToLong(in.substring(32, 36))));
            //4字节-倾斜角
            runningStateDto.setTiltAngle(DecoderUtil.get1032Value(in.substring(36, 44)));
            //1字节-RSSI
            runningStateDto.setRssi(String.valueOf(Integer.valueOf(in.substring(44, 46), 16).shortValue()));
            //2字节-RSRP
            runningStateDto.setRsrp(String.valueOf(Integer.valueOf(in.substring(46, 50), 16).shortValue()));
            //4字节-纬度
            runningStateDto.setLatitude(in.substring(50, 58));
            //4字节-经度
            runningStateDto.setLongitude(in.substring(58, 66));
            //4字节-排放口液位
            runningStateDto.setLevel(DecoderUtil.get1032Value(in.substring(66, 74)));
            //1字节-RS485通道变量个数
            Integer num = Integer.parseInt(in.substring(74, 76), 16);
            runningStateDto.setR485ChannelNum(num);
            List<String> valueList = new ArrayList<>();
            //0~X字节-RS485通道各变量值，每个变量值4字节
            if (num > 0) {
                String dataStr = in.substring(76, 76 + num * 8);
                for (int a = 0; a < dataStr.length() / 8; a++) {
                    valueList.add(DecoderUtil.get1032Value(dataStr.substring(a * 8, a * 8 + 8)));
                }
            }
            runningStateDto.setR485ChannelValue(valueList);
            List<String> valueList2 = new ArrayList<>();
            //4字节-模拟通道1值
            valueList2.add(DecoderUtil.get1032Value(in.substring(76 + num * 8, 84 + num * 8)));
            //4字节-模拟通道2值
            valueList2.add(DecoderUtil.get1032Value(in.substring(84 + num * 8, 92 + num * 8)));
            //4字节-模拟通道3值
            valueList2.add(DecoderUtil.get1032Value(in.substring(92 + num * 8, 100 + num * 8)));
            //4字节-模拟通道4值
            valueList2.add(DecoderUtil.get1032Value(in.substring(100 + num * 8, 108 + num * 8)));
            runningStateDto.setAnalogChannelValue(valueList2);
            //6字节-数据采集时间戳
            runningStateDto.setSampleTime(DecoderUtil.parseDate(in.substring(108 + num * 8, 120 + num * 8)));
            Date utcDate = DateUtil.parseStr2Date(runningStateDto.getRealTimeClock());
            //服务器实时时间超过设备RTC实时时钟1小时，判定时间异常
            if (System.currentTimeMillis() - utcDate.getTime() > 1000 * 60 * 60) {
                runningStateDto.setFaultState(StaticFinalWard.TIME_ERROR);
            }
        } catch (Exception e) {
            log.error("实时报文解析异常！");
        }
    }

    public static RunningStateDto parseDto(String in) {
        RunningStateDto runningStateDto = new RunningStateDto();
        try {
            //1字节-运行状态
            String run = in.substring(0, 2);
            switch (run) {
                case "0":
                    runningStateDto.setRunState("待机");
                    break;
                case "1":
                    runningStateDto.setRunState("运行");
                    break;
                case "2":
                    runningStateDto.setRunState("维护");
                    break;
                case "3":
                    runningStateDto.setRunState("检修");
                    break;
                default:
                    break;
            }
            //1字节-故障状态码
            String fault = in.substring(2, 4);
            runningStateDto.setFaultState("00".equals(fault) ? "正常" : "故障代码" + fault);
            //6字节-实时RTC值
            runningStateDto.setRealTimeClock(DecoderUtil.parseDate(in.substring(4, 16)));
            //2字节-设备内电压
            runningStateDto.setInternalVoltage(String.valueOf(Integer.valueOf(in.substring(16, 20), 16) / 100));
            //2字节-设备外电压
            runningStateDto.setExternalVoltage(String.valueOf(Integer.valueOf(in.substring(20, 24), 16) / 100));
            //2字节-传感器供电电压
            runningStateDto.setSensorVoltage(String.valueOf(Integer.valueOf(in.substring(24, 28), 16) / 100));
            //2字节-温度
            runningStateDto.setTemp(String.valueOf(DecoderUtil.dwordBytesToLong(in.substring(28, 32))));
            //2字节-湿度
            runningStateDto.setHumidity(String.valueOf(DecoderUtil.dwordBytesToLong(in.substring(32, 36))));
            //4字节-倾斜角
            runningStateDto.setTiltAngle(DecoderUtil.get1032Value(in.substring(36, 44)));
            //1字节-RSSI
            runningStateDto.setRssi(String.valueOf(Integer.valueOf(in.substring(44, 46), 16)));
            //2字节-RSRP
            runningStateDto.setRsrp(String.valueOf(Integer.valueOf(in.substring(46, 50), 16)));
            //4字节-纬度
            runningStateDto.setLatitude(in.substring(50, 58));
            //4字节-经度
            runningStateDto.setLongitude(in.substring(58, 66));
            //4字节-排放口液位
            runningStateDto.setLevel(DecoderUtil.get1032Value(in.substring(66, 74)));
            //1字节-RS485通道变量个数
            Integer num = Integer.parseInt(in.substring(74, 76), 16);
            runningStateDto.setR485ChannelNum(num);
            List<String> valueList = new ArrayList<>();
            //0~X字节-RS485通道各变量值，每个变量值4字节
            if (num > 0) {
                String dataStr = in.substring(76, 76 + num * 8);
                for (int a = 0; a < dataStr.length() / 4; a++) {
                    valueList.add(dataStr.substring(a * 8, a * 8 + 8));
                }
            }
            runningStateDto.setR485ChannelValue(valueList);
            List<String> valueList2 = new ArrayList<>();
            //4字节-模拟通道1值
            valueList2.add(DecoderUtil.get1032Value(in.substring(76 + num * 8, 84 + num * 8)));
            //4字节-模拟通道2值
            valueList2.add(DecoderUtil.get1032Value(in.substring(84 + num * 8, 92 + num * 8)));
            //4字节-模拟通道3值
            valueList2.add(DecoderUtil.get1032Value(in.substring(92 + num * 8, 100 + num * 8)));
            //4字节-模拟通道4值
            valueList2.add(DecoderUtil.get1032Value(in.substring(100 + num * 8, 108 + num * 8)));
            runningStateDto.setAnalogChannelValue(valueList2);
            //6字节-数据采集时间戳
            runningStateDto.setSampleTime(DecoderUtil.parseDate(in.substring(108 + num * 8, 120 + num * 8)));
            //服务器实时时间超过设备RTC实时时钟1小时，判定时间异常
            if (System.currentTimeMillis() - DateUtil.parseStr2Date(runningStateDto.getRealTimeClock()).getTime() > 1000 * 60 * 60) {
                runningStateDto.setFaultState(StaticFinalWard.TIME_ERROR);
            }
        } catch (Exception e) {
            log.error("实时报文解析异常");
        }
        return runningStateDto;
    }

}
