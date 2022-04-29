package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @Date: 2020/7/9 11:20
 * @Description: TODO →参数询问返回
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QuerySettingBackDto extends CommonDto {

    /**
     * 硬件版本号
     */
    String hardwareVersion;

    /**
     * 软件版本号
     */
    String softwareVersion;

    /**
     * 产品序列号
     */
    String serialNumber;

    /**
     * 现场机时间
     */
    String dateTime;

    /**
     * 区域代码
     */
    String areaCodeInfo;

    /**
     * 设备地址号
     */
    Integer address;

    /**
     * 运行模式
     */
    String runModel;

    /**
     * RTC自动更新开关
     */
    String rtcAuto;

    /**
     * 液位预警值
     */
    String levelWarnValue;

    /**
     * 采集周期
     */
    String sampleCycle;

    /**
     * 正常上报时间
     */
    String normalReportInterval;

    /**
     * 预警上报时间
     */
    String warnReportInterval;

    /**
     * 窨井深度
     */
    String depth;

    /**
     * 液位校准补偿
     */
    String levelCompensate;

    /**
     * 4-20mA 因子1类型
     */
    String factor1Type;

    /**
     * 4-20mA 因子1量程
     */
    String factor1Range;

    /**
     * 4-20mA 因子2类型
     */
    String factor2Type;

    /**
     * 4-20mA 因子2量程
     */
    String factor2Range;

    /**
     * 4-20mA 因子3类型
     */
    String factor3Type;

    /**
     * 4-20mA 因子3量程
     */
    String factor3Range;

    /**
     * 4-20mA 因子4类型
     */
    String factor4Type;

    /**
     * 4-20mA 因子4量程
     */
    String factor4Range;

    /**
     * 数据中心1 IP
     */
    String dataCenter1Ip;

    /**
     * 数据中心1 端口
     */
    Integer dataCenter1Port;

    /**
     * 数据中心1 通讯方式
     */
    String dataCenter1Model;

    /**
     * 数据中心2 IP
     */
    String dataCenter2Ip;

    /**
     * 数据中心2 端口
     */
    Integer dataCenter2Port;

    /**
     * 数据中心2 通讯方式
     */
    String dataCenter2Model;

    /**
     * 数据中心3 IP
     */
    String dataCenter3Ip;

    /**
     * 数据中心3 端口
     */
    Integer dataCenter3Port;

    /**
     * 数据中心3 通讯方式
     */
    String dataCenter3Model;

    /**
     * RS485 串口波特率
     */
    String baudRate;

    /**
     * 数据位数
     */
    String dataDigit;

    /**
     * 校验位数
     */
    String checkDigit;

    /**
     * 停止位数
     */
    String stopDigit;

    /**
     * RS485串口功耗控制
     */
    String serialPortPowerControl;

    /**
     * 4-20mA通道1功耗控制
     */
    String channel1PowerControl;

    /**
     * 4-20mA通道2功耗控制
     */
    String channel2PowerControl;

    /**
     * 4-20mA通道3功耗控制
     */
    String channel3PowerControl;

    /**
     * 4-20mA通道4功耗控制
     */
    String channel4PowerControl;

    /**
     * 内置超声波液位计功耗控制
     */
    String ultrasonicLevelControl;

    /**
     * 信号质量
     */
    String signalQuality;

    /**
     * ADC1点位1校准值
     */
    String adc1_1Check;

    /**
     * ADC1点位1实际值
     */
    String adc1_1Value;

    /**
     * ADC1点位2校准值
     */
    String adc1_2Check;

    /**
     * ADC1点位2实际值
     */
    String adc1_2Value;

    /**
     * ADC2点位1校准值
     */
    String adc2_1Check;

    /**
     * ADC2点位1实际值
     */
    String adc2_1Value;

    /**
     * ADC2点位2校准值
     */
    String adc2_2Check;

    /**
     * ADC2点位2实际值
     */
    String adc2_2Value;

    /**
     * ADC3点位1校准值
     */
    String adc3_1Check;

    /**
     * ADC3点位1实际值
     */
    String adc3_1Value;

    /**
     * ADC3点位2校准值
     */
    String adc3_2Check;

    /**
     * ADC3点位2实际值
     */
    String adc3_2Value;

    /**
     * ADC4点位1校准值
     */
    String adc4_1Check;

    /**
     * ADC4点位1实际值
     */
    String adc4_1Value;

    /**
     * ADC4点位2校准值
     */
    String adc4_2Check;

    /**
     * ADC4点位2实际值
     */
    String adc4_2Value;

    /**
     * RS485设备上电等待时间
     */
    String rs485WaitPowerOnTime;

    /**
     * RS485总线挂接设备个数
     */
    String rs485DeviceNum;

    /**
     * RS485变量个数
     */
    String rs485VariateNum;

    /**
     * RS485变量1设备地址
     */
    String rs485Variate1Address;

    /**
     * RS485变量1功能码
     */
    String rs485Variate1FunctionCode;

    /**
     * RS485变量1起始寄存器号
     */
    String rs485Variate1StartNo;

    /**
     * RS485变量1数据类型
     */
    String rs485Variate1DataType;

    /**
     * RS485变量1数据精度/浮点数字节组合方式
     */
    String rs485Variate1ByteMode;

    /**
     * RS485变量2设备地址
     */
    String rs485Variate2Address;

    /**
     * RS485变量2功能码
     */
    String rs485Variate2FunctionCode;

    /**
     * RS485变量2起始寄存器号
     */
    String rs485Variate2StartNo;

    /**
     * RS485变量2数据类型
     */
    String rs485Variate2DataType;

    /**
     * RS485变量2数据精度/浮点数字节组合方式
     */
    String rs485Variate2ByteMode;

    /**
     * RS485变量3设备地址
     */
    String rs485Variate3Address;

    /**
     * RS485变量3功能码
     */
    String rs485Variate3FunctionCode;

    /**
     * RS485变量3起始寄存器号
     */
    String rs485Variate3StartNo;

    /**
     * RS485变量3数据类型
     */
    String rs485Variate3DataType;

    /**
     * RS485变量3数据精度/浮点数字节组合方式
     */
    String rs485Variate3ByteMode;

    /**
     * RS485变量4设备地址
     */
    String rs485Variate4Address;

    /**
     * RS485变量4功能码
     */
    String rs485Variate4FunctionCode;

    /**
     * RS485变量4起始寄存器号
     */
    String rs485Variate4StartNo;

    /**
     * RS485变量4数据类型
     */
    String rs485Variate4DataType;

    /**
     * RS485变量4数据精度/浮点数字节组合方式
     */
    String rs485Variate4ByteMode;

    /**
     * RS485变量5设备地址
     */
    String rs485Variate5Address;

    /**
     * RS485变量5功能码
     */
    String rs485Variate5FunctionCode;


    /**
     * RS485变量5起始寄存器号
     */
    String rs485Variate5StartNo;

    /**
     * RS485变量5数据类型
     */
    String rs485Variate5DataType;

    /**
     * RS485变量5数据精度/浮点数字节组合方式
     */
    String rs485Variate5ByteMode;

    /**
     * RS485变量6设备地址
     */
    String rs485Variate6Address;

    /**
     * RS485变量6功能码
     */
    String rs485Variate6FunctionCode;

    /**
     * RS485变量6起始寄存器号
     */
    String rs485Variate6StartNo;

    /**
     * RS485变量6数据类型
     */
    String rs485Variate6DataType;

    /**
     * RS485变量6数据精度/浮点数字节组合方式
     */
    String rs485Variate6ByteMode;

    /**
     * RS485变量7设备地址
     */
    String rs485Variate7Address;

    /**
     * RS485变量7功能码
     */
    String rs485Variate7FunctionCode;

    /**
     * RS485变量7起始寄存器号
     */
    String rs485Variate7StartNo;

    /**
     * RS485变量7数据类型
     */
    String rs485Variate7DataType;

    /**
     * RS485变量7数据精度/浮点数字节组合方式
     */
    String rs485Variate7ByteMode;

    /**
     * RS485变量8设备地址
     */
    String rs485Variate8Address;

    /**
     * RS485变量8功能码
     */
    String rs485Variate8FunctionCode;

    /**
     * RS485变量8起始寄存器号
     */
    String rs485Variate8StartNo;

    /**
     * RS485变量8数据类型
     */
    String rs485Variate8DataType;

    /**
     * RS485变量8数据精度/浮点数字节组合方式
     */
    String rs485Variate8ByteMode;

    /**
     * RS485变量9设备地址
     */
    String rs485Variate9Address;

    /**
     * RS485变量9功能码
     */
    String rs485Variate9FunctionCode;

    /**
     * RS485变量9起始寄存器号
     */
    String rs485Variate9StartNo;

    /**
     * RS485变量9数据类型
     */
    String rs485Variate9DataType;

    /**
     * RS485变量9数据精度/浮点数字节组合方式
     */
    String rs485Variate9ByteMode;

    /**
     * RS485变量10设备地址
     */
    String rs485Variate10Address;

    /**
     * RS485变量10功能码
     */
    String rs485Variate10FunctionCode;

    /**
     * RS485变量10起始寄存器号
     */
    String rs485Variate10StartNo;

    /**
     * RS485变量10数据类型
     */
    String rs485Variate10DataType;

    /**
     * RS485变量10数据精度/浮点数字节组合方式
     */
    String rs485Variate10ByteMode;

    /**
     * RS485变量11设备地址
     */
    String rs485Variate11Address;

    /**
     * RS485变量11功能码
     */
    String rs485Variate11FunctionCode;

    /**
     * RS485变量11起始寄存器号
     */
    String rs485Variate11StartNo;

    /**
     * RS485变量11数据类型
     */
    String rs485Variate11DataType;

    /**
     * RS485变量11数据精度/浮点数字节组合方式
     */
    String rs485Variate11ByteMode;

    /**
     * RS485变量12设备地址
     */
    String rs485Variate12Address;

    /**
     * RS485变量12功能码
     */
    String rs485Variate12FunctionCode;

    /**
     * RS485变量12起始寄存器号
     */
    String rs485Variate12StartNo;

    /**
     * RS485变量12数据类型
     */
    String rs485Variate12DataType;

    /**
     * RS485变量12数据精度/浮点数字节组合方式
     */
    String rs485Variate12ByteMode;

    /**
     * ADC1通道延时时间
     */
    String adc1ChannelDelayTime;

    /**
     * ADC2通道延时时间
     */
    String adc2ChannelDelayTime;

    /**
     * ADC3通道延时时间
     */
    String adc3ChannelDelayTime;

    /**
     * ADC4通道延时时间
     */
    String adc4ChannelDelayTime;

    /**
     * IMEI
     */
    String IMEI;

    /**
     * CCID
     */
    String CCID;

    @Override
    protected void decode(String in, CommonDto commonDto) {
        QuerySettingBackDto dto = (QuerySettingBackDto) commonDto;
        if (commonDto.getLength() > 50) {
            //硬件版本号-2字节
            dto.setHardwareVersion(String.valueOf(Integer.valueOf(in.substring(0, 4), 16)));
            //软件版本号-6字节
            dto.setSoftwareVersion(Integer.valueOf(in.substring(4, 8), 16) + " - " + DecoderUtil.parseDate2(in.substring(8, 16)));
            //产品序列号-16字节
            dto.setSerialNumber(in.substring(16, 48));
            //现场机时间-6字节
            dto.setDateTime(DecoderUtil.parseDate(in.substring(48, 60)));
            //区域代码-2字节
            dto.setAreaCodeInfo(String.valueOf(Integer.valueOf(in.substring(60, 64), 16)));
            //设备地址-2字节
            dto.setAddress(Integer.valueOf(in.substring(64, 68), 16));
            //运行模式-1字节
            switch (Integer.valueOf(in.substring(68, 70), 16)) {
                case 0:
                    dto.setRunModel("短链接上报");
                    break;
                case 1:
                    dto.setRunModel("长链接上报");
                    break;
                case 2:
                    dto.setRunModel("唤醒模式");
                    break;
                default:
                    break;
            }
            //RTC自动更新开关-1字节
            switch (Integer.valueOf(in.substring(70, 72), 16)) {
                case 0:
                    dto.setRunModel("关闭");
                    break;
                case 1:
                    dto.setRunModel("开启");
                    break;
                default:
                    break;
            }
            //液位预警值-4字节
            dto.setLevelWarnValue(DecoderUtil.get1032Value(in.substring(72, 80)) + " 米");
            //数据采集周期-1字节
            dto.setSampleCycle(Integer.valueOf(in.substring(80, 82), 16) + " 分钟");
            //正常上报时间间隔-1字节
            dto.setNormalReportInterval(Integer.valueOf(in.substring(82, 84), 16) + " 分钟");
            //预警上报时间间隔-1字节
            dto.setWarnReportInterval(Integer.valueOf(in.substring(84, 86), 16) + " 分钟");
            //窨井深度-4字节
            dto.setDepth(Double.valueOf(DecoderUtil.get1032Value(in.substring(86, 94))) + " 米");
            //液位校准补偿-4字节
            dto.setLevelCompensate(DecoderUtil.get1032Value(in.substring(94, 102)) + " 米");
            //4-20mA 因子1类型-1字节
            dto.setFactor1Type(getFactorType(in.substring(102, 104)));
            //4-20mA 因子1量程-4字节
            dto.setFactor1Range(DecoderUtil.get1032Value(in.substring(104, 112)));
            //4-20mA 因子2类型-1字节
            dto.setFactor2Type(getFactorType(in.substring(112, 114)));
            //4-20mA 因子2量程-4字节
            dto.setFactor2Range(DecoderUtil.get1032Value(in.substring(114, 122)));
            //4-20mA 因子3类型-1字节
            dto.setFactor3Type(getFactorType(in.substring(122, 124)));
            //4-20mA 因子3量程-4字节
            dto.setFactor3Range(DecoderUtil.get1032Value(in.substring(124, 132)));
            //4-20mA 因子4类型-1字节
            dto.setFactor4Type(getFactorType(in.substring(132, 134)));
            //4-20mA 因子4量程-4字节
            dto.setFactor4Range(DecoderUtil.get1032Value(in.substring(134, 142)));
            //数据中心1 IP-4字节
            dto.setDataCenter1Ip(Integer.valueOf(in.substring(142, 144), 16) + "." + Integer.valueOf(in.substring(144, 146), 16) + "." + Integer.valueOf(in.substring(146, 148), 16) + "." + Integer.valueOf(in.substring(148, 150), 16));
            //数据中心1 端口-2字节
            dto.setDataCenter1Port(Integer.valueOf(in.substring(150, 154), 16));
            //数据中心1 通讯方式-1字节
            dto.setDataCenter1Model(Integer.valueOf(in.substring(154, 156), 16) == 0 ? "tcp" : "UDP");
            //数据中心2 IP-4字节
            dto.setDataCenter2Ip(Integer.valueOf(in.substring(156, 158), 16) + "." + Integer.valueOf(in.substring(158, 160), 16) + "." + Integer.valueOf(in.substring(160, 162), 16) + "." + Integer.valueOf(in.substring(162, 164), 16));
            //数据中心2 端口-2字节
            dto.setDataCenter2Port(Integer.valueOf(in.substring(164, 168), 16));
            //数据中心2 通讯方式-1字节
            dto.setDataCenter2Model(Integer.valueOf(in.substring(168, 170), 16) == 0 ? "tcp" : "UDP");
            //数据中心3 IP-4字节
            dto.setDataCenter3Ip(Integer.valueOf(in.substring(170, 172), 16) + "." + Integer.valueOf(in.substring(172, 174), 16) + "." + Integer.valueOf(in.substring(174, 176), 16) + "." + Integer.valueOf(in.substring(176, 178), 16));
            //数据中心3 端口-2字节
            dto.setDataCenter3Port(Integer.valueOf(in.substring(178, 182), 16));
            //数据中心3 通讯方式-1字节
            dto.setDataCenter3Model(Integer.valueOf(in.substring(182, 184), 16) == 0 ? "tcp" : "UDP");
            //RS485串口波特率-1字节
            dto.setBaudRate(forBaudRate(in.substring(184, 186)));
            //数据位数-1字节
            dto.setDataDigit(forDataDigit(in.substring(186, 188)));
            //校验位数-1字节
            dto.setCheckDigit(forCheckDigit(in.substring(188, 190)));
            //停止位数-1字节
            dto.setStopDigit(forStopDigit(in.substring(190, 192)));
            //RS485串口功耗控制-1字节
            dto.setSerialPortPowerControl(Integer.valueOf(in.substring(192, 194), 16) == 0 ? "关闭" : "开启");
            //4-20mA通道1功耗控制-1字节
            dto.setChannel1PowerControl(Integer.valueOf(in.substring(194, 196), 16) == 0 ? "关闭" : "开启");
            //4-20mA通道2功耗控制-1字节
            dto.setChannel2PowerControl(Integer.valueOf(in.substring(196, 198), 16) == 0 ? "关闭" : "开启");
            //4-20mA通道3功耗控制-1字节
            dto.setChannel3PowerControl(Integer.valueOf(in.substring(198, 200), 16) == 0 ? "关闭" : "开启");
            //4-20mA通道4功耗控制-1字节
            dto.setChannel4PowerControl(Integer.valueOf(in.substring(200, 202), 16) == 0 ? "关闭" : "开启");
            //内置超声波液位计功耗控制-1字节
            dto.setUltrasonicLevelControl(Integer.valueOf(in.substring(202, 204), 16) == 0 ? "关闭" : "开启");
            //信号质量-1字节
            dto.setSignalQuality(String.valueOf(Integer.valueOf(in.substring(204, 206), 16)));
            //ADC1点位1校准值-2字节
            dto.setAdc1_1Check(Integer.valueOf(in.substring(206, 210), 16) / 100 + " mA");
            //ADC1点位1实际值-4字节
            dto.setAdc1_1Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(210, 218))) + " mA");
            dto.setAdc1_2Check(Integer.valueOf(in.substring(218, 222), 16) / 100 + " mA");
            dto.setAdc1_2Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(222, 230))) + " mA");
            dto.setAdc2_1Check(Integer.valueOf(in.substring(230, 234), 16) / 100 + " mA");
            dto.setAdc2_1Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(234, 242))) + " mA");
            dto.setAdc2_2Check(Integer.valueOf(in.substring(242, 246), 16) / 100 + " mA");
            dto.setAdc2_2Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(246, 254))) + " mA");
            dto.setAdc3_1Check(Integer.valueOf(in.substring(254, 258), 16) / 100 + " mA");
            dto.setAdc3_1Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(258, 266))) + " mA");
            dto.setAdc3_2Check(Integer.valueOf(in.substring(266, 270), 16) / 100 + " mA");
            dto.setAdc3_2Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(270, 278))) + " mA");
            dto.setAdc4_1Check(Integer.valueOf(in.substring(278, 282), 16) / 100 + " mA");
            dto.setAdc4_1Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(282, 290))) + " mA");
            dto.setAdc4_2Check(Integer.valueOf(in.substring(290, 294), 16) / 100 + " mA");
            dto.setAdc4_2Value(Double.valueOf(DecoderUtil.get1032Value(in.substring(294, 302))) + " mA");
            //RS485设备上电等待时间-1字节
            dto.setRs485WaitPowerOnTime(Integer.valueOf(in.substring(302, 304), 16) + " 秒");
            //RS485总线挂接设备个数-1字节
            dto.setRs485DeviceNum(String.valueOf(Integer.valueOf(in.substring(304, 306), 16)));
            //RS485变量个数-1字节
            dto.setRs485VariateNum(String.valueOf(Integer.valueOf(in.substring(306, 308), 16)));
            //RS485变量1的设备地址-1字节
            dto.setRs485Variate1Address(String.valueOf(Integer.valueOf(in.substring(308, 310), 16)));
            //RS485变量1功能码-1字节
            dto.setRs485Variate1FunctionCode(forFunctionCode(Integer.valueOf(in.substring(310, 312), 16)));
            //RS485变量1起始寄存器号-2字节
            dto.setRs485Variate1StartNo(String.valueOf(Integer.valueOf(in.substring(312, 316), 16)));
            //RS485变量1数据类型-1字节
            String dataType1 = in.substring(316, 318);
            dto.setRs485Variate1DataType(forDataType(dataType1));
            //RS485变量1数据精度/浮点数字节组合方式-1字节
            String byteModel1 = in.substring(318, 320);
            dto.setRs485Variate1ByteMode(forByteModel(dataType1, byteModel1));

            //RS485变量2的设备地址-1字节
            dto.setRs485Variate2Address(String.valueOf(Integer.valueOf(in.substring(320, 322), 16)));
            //RS485变量2功能码-1字节
            dto.setRs485Variate2FunctionCode(forFunctionCode(Integer.valueOf(in.substring(322, 324), 16)));
            //RS485变量2起始寄存器号-2字节
            dto.setRs485Variate2StartNo(String.valueOf(Integer.valueOf(in.substring(324, 328), 16)));
            //RS485变量2数据类型-1字节
            String dataType2 = in.substring(328, 330);
            dto.setRs485Variate2DataType(forDataType(dataType2));
            //RS485变量2数据精度/浮点数字节组合方式-1字节
            String byteModel2 = in.substring(330, 332);
            dto.setRs485Variate2ByteMode(forByteModel(dataType2, byteModel2));

            dto.setRs485Variate3Address(String.valueOf(Integer.valueOf(in.substring(332, 334), 16)));
            dto.setRs485Variate3FunctionCode(forFunctionCode(Integer.valueOf(in.substring(334, 336), 16)));
            dto.setRs485Variate3StartNo(String.valueOf(Integer.valueOf(in.substring(336, 340), 16)));
            String dataType3 = in.substring(340, 342);
            dto.setRs485Variate3DataType(forDataType(dataType3));
            String byteModel3 = in.substring(342, 344);
            dto.setRs485Variate3ByteMode(forByteModel(dataType3, byteModel3));

            dto.setRs485Variate4Address(String.valueOf(Integer.valueOf(in.substring(344, 346), 16)));
            dto.setRs485Variate4FunctionCode(forFunctionCode(Integer.valueOf(in.substring(346, 348), 16)));
            dto.setRs485Variate4StartNo(String.valueOf(Integer.valueOf(in.substring(348, 352), 16)));
            String dataType4 = in.substring(352, 354);
            dto.setRs485Variate4DataType(forDataType(dataType4));
            String byteModel4 = in.substring(354, 356);
            dto.setRs485Variate4ByteMode(forByteModel(dataType4, byteModel4));

            dto.setRs485Variate5Address(String.valueOf(Integer.valueOf(in.substring(356, 358), 16)));
            dto.setRs485Variate5FunctionCode(forFunctionCode(Integer.valueOf(in.substring(358, 360), 16)));
            dto.setRs485Variate5StartNo(String.valueOf(Integer.valueOf(in.substring(360, 364), 16)));
            String dataType5 = in.substring(364, 366);
            dto.setRs485Variate5DataType(forDataType(dataType5));
            String byteModel5 = in.substring(366, 368);
            dto.setRs485Variate5ByteMode(forByteModel(dataType5, byteModel5));

            dto.setRs485Variate6Address(String.valueOf(Integer.valueOf(in.substring(368, 370), 16)));
            dto.setRs485Variate6FunctionCode(forFunctionCode(Integer.valueOf(in.substring(370, 372), 16)));
            dto.setRs485Variate6StartNo(String.valueOf(Integer.valueOf(in.substring(372, 376), 16)));
            String dataType6 = in.substring(376, 378);
            dto.setRs485Variate6DataType(forDataType(dataType6));
            String byteModel6 = in.substring(378, 380);
            dto.setRs485Variate6ByteMode(forByteModel(dataType6, byteModel6));

            dto.setRs485Variate7Address(String.valueOf(Integer.valueOf(in.substring(380, 382), 16)));
            dto.setRs485Variate7FunctionCode(forFunctionCode(Integer.valueOf(in.substring(382, 384), 16)));
            dto.setRs485Variate7StartNo(String.valueOf(Integer.valueOf(in.substring(384, 388), 16)));
            String dataType7 = in.substring(388, 390);
            dto.setRs485Variate7DataType(forDataType(dataType7));
            String byteModel7 = in.substring(390, 392);
            dto.setRs485Variate7ByteMode(forByteModel(dataType7, byteModel7));

            dto.setRs485Variate8Address(String.valueOf(Integer.valueOf(in.substring(392, 394), 16)));
            dto.setRs485Variate8FunctionCode(forFunctionCode(Integer.valueOf(in.substring(394, 396), 16)));
            dto.setRs485Variate8StartNo(String.valueOf(Integer.valueOf(in.substring(396, 400), 16)));
            String dataType8 = in.substring(400, 402);
            dto.setRs485Variate8DataType(forDataType(dataType8));
            String byteModel8 = in.substring(402, 404);
            dto.setRs485Variate8ByteMode(forByteModel(dataType8, byteModel8));

            dto.setRs485Variate9Address(String.valueOf(Integer.valueOf(in.substring(404, 406), 16)));
            dto.setRs485Variate9FunctionCode(forFunctionCode(Integer.valueOf(in.substring(406, 408), 16)));
            dto.setRs485Variate9StartNo(String.valueOf(Integer.valueOf(in.substring(408, 412), 16)));
            String dataType9 = in.substring(412, 414);
            dto.setRs485Variate9DataType(forDataType(dataType9));
            String byteModel9 = in.substring(414, 416);
            dto.setRs485Variate9ByteMode(forByteModel(dataType9, byteModel9));

            dto.setRs485Variate10Address(String.valueOf(Integer.valueOf(in.substring(416, 418), 16)));
            dto.setRs485Variate10FunctionCode(forFunctionCode(Integer.valueOf(in.substring(418, 420), 16)));
            dto.setRs485Variate10StartNo(String.valueOf(Integer.valueOf(in.substring(420, 424), 16)));
            String dataType10 = in.substring(424, 426);
            dto.setRs485Variate10DataType(forDataType(dataType10));
            String byteModel10 = in.substring(426, 428);
            dto.setRs485Variate10ByteMode(forByteModel(dataType10, byteModel10));

            dto.setRs485Variate11Address(String.valueOf(Integer.valueOf(in.substring(428, 430), 16)));
            dto.setRs485Variate11FunctionCode(forFunctionCode(Integer.valueOf(in.substring(430, 432), 16)));
            dto.setRs485Variate11StartNo(String.valueOf(Integer.valueOf(in.substring(432, 436), 16)));
            String dataType11 = in.substring(436, 438);
            dto.setRs485Variate11DataType(forDataType(dataType11));
            String byteModel11 = in.substring(438, 440);
            dto.setRs485Variate11ByteMode(forByteModel(dataType11, byteModel11));

            dto.setRs485Variate12Address(String.valueOf(Integer.valueOf(in.substring(440, 442), 16)));
            dto.setRs485Variate12FunctionCode(forFunctionCode(Integer.valueOf(in.substring(442, 444), 16)));
            dto.setRs485Variate12StartNo(String.valueOf(Integer.valueOf(in.substring(444, 448), 16)));
            String dataType12 = in.substring(448, 450);
            dto.setRs485Variate12DataType(forDataType(dataType12));
            String byteModel12 = in.substring(450, 452);
            dto.setRs485Variate12ByteMode(forByteModel(dataType12, byteModel12));

            //ADC1通道延时时间
            dto.setAdc1ChannelDelayTime(String.valueOf(Integer.valueOf(in.substring(452, 454), 16)));
            //ADC2通道延时时间
            dto.setAdc2ChannelDelayTime(String.valueOf(Integer.valueOf(in.substring(454, 456), 16)));
            //ADC3通道延时时间
            dto.setAdc3ChannelDelayTime(String.valueOf(Integer.valueOf(in.substring(456, 458), 16)));
            //ADC4通道延时时间
            dto.setAdc4ChannelDelayTime(String.valueOf(Integer.valueOf(in.substring(458, 460), 16)));
        } else if (commonDto.getLength() > 15) {
            //IMEI
            dto.setIMEI(hex2Str(in.substring(0, 30)));
            //CCID
            dto.setCCID(hex2Str(in.substring(30, 70)));
        } else {
            //硬件版本号-2字节
            dto.setHardwareVersion(String.valueOf(Integer.valueOf(in.substring(0, 4), 16)));
        }
    }

    private String forFunctionCode(Integer code) {
        String functionStr = "";
        switch (code) {
            case 3:
                functionStr = "4XXXX寄存器区";
                break;
            case 4:
                functionStr = "3XXXX寄存器区";
                break;
            default:
                functionStr = "未设置";
                break;
        }
        return functionStr;
    }

    private String forByteModel(String dateTypeStr, String model) {
        Integer dateType = Integer.valueOf(dateTypeStr);
        String type = "";
        switch (Integer.valueOf(model)) {
            case 1:
                if (dateType < 3) {
                    type = "整型1精度";
                } else {
                    type = "浮点数ABCD";
                }
                break;
            case 2:
                if (dateType < 3) {
                    type = "整型0.1精度";
                } else {
                    type = "浮点数BADC";
                }
                break;
            case 3:
                if (dateType < 3) {
                    type = "整型0.01精度";
                } else {
                    type = "浮点数DCBA";
                }
                break;
            case 4:
                if (dateType < 3) {
                    type = "整型0.001精度";
                } else {
                    type = "浮点数CDAB";
                }
                break;
            case 5:
                type = "双精度ABCDEFGH";
                break;
            case 6:
                type = "双精度BADCFEHG";
                break;
            case 7:
                type = "双精度HGFEDCBA";
                break;
            case 8:
                type = "双精度GHEFCDAB";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String forDataType(String toHex) {
        String type = "";
        switch (Integer.valueOf(toHex)) {
            case 1:
                type = "16位整型";
                break;
            case 2:
                type = "32位整型";
                break;
            case 3:
                type = "单精度浮点数";
                break;
            case 4:
                type = "双精度浮点数";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String forStopDigit(String toHex) {
        String type = "";
        switch (Integer.valueOf(toHex)) {
            case 0:
                type = "1位";
                break;
            case 1:
                type = "1.5位";
                break;
            case 2:
                type = "2位";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String forCheckDigit(String toHex) {
        String type = "";
        switch (Integer.valueOf(toHex)) {
            case 0:
                type = "无";
                break;
            case 1:
                type = "奇校验";
                break;
            case 2:
                type = "偶校验";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String forDataDigit(String toHex) {
        String type = "";
        switch (Integer.valueOf(toHex)) {
            case 0:
                type = "6位";
                break;
            case 1:
                type = "7位";
                break;
            case 2:
                type = "8位";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String forBaudRate(String toHex) {
        String type = "";
        switch (Integer.parseInt(toHex, 16)) {
            case 0:
                type = "2400";
                break;
            case 1:
                type = "4800";
                break;
            case 2:
                type = "9600";
                break;
            case 3:
                type = "14400";
                break;
            case 4:
                type = "19200";
                break;
            case 5:
                type = "38400";
                break;
            case 6:
                type = "56000";
                break;
            case 7:
                type = "57600";
                break;
            case 8:
                type = "115200";
                break;
            default:
                type = "未设置";
                break;
        }
        return type;
    }

    private String getFactorType(String toHex) {
        String type = "";
        switch (Integer.valueOf(toHex, 16)) {
            case 0:
                type = "瞬时流量";
                break;
            case 1:
                type = "COD";
                break;
            case 2:
                type = "氨氮";
                break;
            case 3:
                type = "总磷";
                break;
            case 4:
                type = "总氮";
                break;
            case 5:
                type = "PH";
                break;
            case 22:
                type = "高猛酸盐";
                break;
            case 27:
                type = "溶解氧";
                break;
            case 28:
                type = "水温";
                break;
            case 29:
                type = "悬浮物";
                break;
            case 30:
                type = "浊度";
                break;
            case 31:
                type = "电导率";
                break;
            case 32:
                type = "液位";
                break;
            case 59:
                type = "叶绿素";
                break;
            case 99:
                type = "压力";
                break;
            case 220:
                type = "瞬时雨量";
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 16进制转ASCII
     *
     * @param hex
     * @return
     */
    private static String hex2Str(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            switch (output) {
                //01起始帧
                case "01":
                    sb.append("SOH");
                    break;
                //02传输正文开始
                case "02":
                    sb.append("STK");
                    break;
                //03报文结束，后续无报文
                case "03":
                    sb.append("ETX");
                    break;
                //传输结束，退出
                case "04":
                    sb.append("EOT");
                    break;
                //询问
                case "05":
                    sb.append("ENQ");
                    break;
                //肯定确认，继续发送
                case "06":
                    sb.append("ACK");
                    break;
                //否定应答，反馈重发
                case "15":
                    sb.append("NAK");
                    break;
                //多包传输正文起始
                case "16":
                    sb.append("SYN");
                    break;
                //报文结束，后续有报文
                case "17":
                    sb.append("ETB");
                    break;
                default:
                    int decimal = Integer.parseInt(output, 16);
                    sb.append((char) decimal);
                    break;
            }
        }
        return sb.toString();
    }
}
