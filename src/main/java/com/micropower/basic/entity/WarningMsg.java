package com.micropower.basic.entity;

import lombok.Data;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/4/27 15:59
 */
@Data
public class WarningMsg {
    /**
     * 号码
     */
    String number;

    /**
     * 站点信息
     */
    StationBean station;

    /**
     * 预警类型
     */
    String warnType;

    /**
     * 标识Key
     */
    String key;

    /**
     * 时间间隔
     */
    int interval;

    /**
     * 内容描述
     */
    String content;

    public WarningMsg(String number, String warnType,StationBean station, String key, int interval, String content) {
        this.number = number;
        this.warnType = warnType;
        this.station = station;
        this.key = key;
        this.interval = interval;
        this.content = content;
    }
}
