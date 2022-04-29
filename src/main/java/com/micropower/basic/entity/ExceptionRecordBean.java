package com.micropower.basic.entity;

import lombok.Data;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/4/27 15:17
 */
@Data
public class ExceptionRecordBean {
    /**
     * 主键ID
     */
    int id;

    /**
     * 站点ID
     */
    int stationId;

    /**
     * 时间
     */
    String time;

    /**
     * 类型
     */
    String type;

    /**
     * 内容
     */
    String content;

    public ExceptionRecordBean() {
    }

    public ExceptionRecordBean(int stationId, String type, String content) {
        this.stationId = stationId;
        this.type = type;
        this.content = content;
    }
}
