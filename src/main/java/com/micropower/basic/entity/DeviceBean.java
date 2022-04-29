package com.micropower.basic.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Kohaku_川
 * @description TODO 设备信息封装类
 * @date 2022/4/27 10:01
 */
@Data
public class DeviceBean {
    /**
     * 主键ID
     */
    int id;

    /**
     * 区块号
     */
    String areaCode;

    /**
     * 设备地址
     */
    int address;

    /**
     * 唯一标识码=区块码-地址号
     */
    String areaAddress;

    /**
     * 名称
     */
    String name;

    /**
     * 区块名
     */
    String area;

    /**
     * 上传时间
     */
    String uploadTime;

    /**
     * 离线判定时间
     */
    int overTime;

    /**
     * rs485串口配置
     */
    String rs485;

    /**
     * 模拟通道配置
     */
    String analog;

    /**
     * 是否监测流量
     */
    String isFlow;

    /**
     * 是否监测液位(超声波液位)
     */
    String isLevel;

    /**
     * 超声波液位预警值
     */
    String levelWarn;

    /**
     * 超声波液位状态
     */
    String levelState;

    /**
     * 超声波液位假数据Min
     */
    String levelFakeMin;

    /**
     * 超声波液位假数据Max
     */
    String levelFakeMax;

    /**
     * 流量计算类别，0.原始值读取，1.公式计算
     */
    String flowCount;

    /**
     * 管径直径/宽度
     */
    String pipeDiameter;

    /**
     * 管径截面形状
     */
    String pipeShape;

    /**
     * 管径限高
     */
    String pipeHeight;

    /**
     * 淤泥深度
     */
    String siltHeight;

    /**
     * 液位对应因子名称
     */
    String levelHeightName;

    /**
     * 流速对应因子名称
     */
    String speedName;

    /**
     * 瞬时流量对应因子名称
     */
    String flowName;

    /**
     * 累积流量对应因子名称
     */
    String totalFlowName;

    /**
     * 三角形流量系数
     */
    String triangleCoefficient;

    /**
     * 是否有传感器
     */
    String isSensor;

    /**
     * 是否监测降雨量
     */
    String isRainfall;

    /**
     * 雨量脉冲值对应的因子名称
     */
    String rainfallPulseName;

    /**
     * 是否AEP
     */
    String isAep;

    /**
     * AEP平台设备ID
     */
    String aepId;

    /**
     * 状态，0.停用，1.启用
     */
    String state;

    /**
     * 网络
     */
    String network;


}
