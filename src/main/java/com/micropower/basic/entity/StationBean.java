package com.micropower.basic.entity;

import lombok.Data;

/**
 * @author Kohaku_川
 * @description TODO 站点数据封装类
 * @date 2022/4/27 9:26
 */
@Data
public class StationBean {
    /**
     * 主键ID
     */
    int id;

    /**
     * 名称
     */
    String name;

    /**
     * 区块ID
     */
    int blockId;

    /**
     * 区块URL
     */
    String blockUrl;

    /**
     * 设备ID
     */
    int deviceId;

    /**
     * 状态 0.调试，1.运行，2.维护
     */
    String state;

    /**
     * 是否转发
     */
    boolean isForwarding;

    /**
     * 是否从设备GPS读取坐标
     */
    boolean gpsRead;

    /**
     * 经度
     */
    String longitude;

    /**
     * 纬度
     */
    String latitude;

    /**
     * 绑定设备信息
     */
    DeviceBean device;

}
