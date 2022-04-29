package com.micropower.basic.entity;

import lombok.Data;

/**
 * @author Kohaku_川
 * @description TODO 实时数据封装类
 * @date 2022/4/27 8:44
 */
@Data
public class RealtimeDataBean {
    /** 主键Id
     */
    Integer id;

    /**站点ID
     */
    Integer stationId;

    /** 经度
     */
    String longitude;

    /**纬度
     */
    String latitude;

    /**网络
     */
    String network;

    /**运行状态
     */
    String runState;

    /**故障状态
     */
    String faultState;

    /**实时RTC
     */
    String rtcTime;

    /**内电压
     */
    String internalVoltage;

    /**外电压
     */
    String externalVoltage;

    /**传感器电压
     */
    String sensorVoltage;

    /**温度
     */
    String temp;

    /**湿度
     */
    String humidity;

    /**倾角
     */
    String tiltAngle;

    /**rssi
     */
    String rssi;

    /**rsrp
     */
    String rsrp;

    /**超声波液位
     */
    String outletLevel;

    /**超声波液位状态
     */
    String levelState;

    /**串口+模拟变量集合
     */
    String valueList;

    /**数据采集时间
     */
    String sampleTime;

    /**数据更新时间
     */
    String updateTime;

    /**瞬时流量
     */
    String flow;

    /**流速
     */
    String speed;

    /**累积流量基数
     */
    String flowBase;

    /**液位高度
     */
    String levelHeight;

    /**排放量
     */
    String emissions;

    /**流量计算间隔
     */
    int flowInterval;

    /**累计流量
     */
    String totalFlow;

    /**数据解析状态
     */
    String dataState;

    /**通讯方式
     */
    String communicationMode;

    /**内电压预警
     */
    boolean voltageWarn;

    /**湿度预警
     */
    boolean humidityWarn;

    /**传感器电压预警
     */
    boolean sensorVoltageWarn;

    /**雨量脉冲值基数
     */
    String pulseBase;

    /**雨量脉冲上传值
     */
    String pulseUpload;

    /**雨量脉冲最终计算值
     */
    String pulseCount;

    /**降雨量
     */
    String rainfall;

    /**降雨强度
     */
    String rainfallIntensity;

    /**雨量计算间隔
     */
    int rainInterval;

}
