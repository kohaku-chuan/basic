package com.micropower.basic.entity;

import lombok.Data;

/**
 * @author Kohaku_川
 * @description 流量转发参数封装类
 * @date 2022/4/26 15:30
 */
@Data
public class FlowForwardBean {

    /**
     * 站点ID
     */
    int stationId;

    /**
     * IP
     */
    String ip;

    /**
     * 端口
     */
    int port;

    /**
     * 转发地址号
     */
    int forwardingAddress;

    /**
     * 液位
     */
    String levelHeight;

    /**
     * 流速
     */
    String speed;

    /**
     * 瞬时流量
     */
    String flow;

    /**
     * 累计流量
     */
    String totalFlow;

    public FlowForwardBean() {
    }

    public FlowForwardBean(int stationId, String ip, int port, int forwardingAddress, String levelHeight, String speed, String flow, String totalFlow) {
        this.stationId = stationId;
        this.ip = ip;
        this.port = port;
        this.forwardingAddress = forwardingAddress;
        this.levelHeight = levelHeight;
        this.speed = speed;
        this.flow = flow;
        this.totalFlow = totalFlow;
    }
}
