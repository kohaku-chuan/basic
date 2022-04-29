package com.micropower.basic.common.dto.receive;

import lombok.Data;

import java.util.List;

/**
 * @Date: 2020/9/18 18:01
 * @Description: TODO →查询后返回的历史记录子记录
 * @Author:Kohaku_川
 **/
@Data
public class HistoryChildRecord {
    /**
     * 地址号
     **/
    private Integer address;

    /**
     * 区域号
     **/
    private String areaCode;

    /**
     * 排口液位
     **/
    private String level;

    /**
     * RS485通道变量个数
     */
    Integer r485ChannelNum;

    /**
     * RS485通道变量值
     */
    List<String> r485ChannelValue;

    /**
     * 模拟通道值
     */
    List<String> analogValue;

    /**
     * 数据采集时间戳
     */
    String sampleTime;
}
