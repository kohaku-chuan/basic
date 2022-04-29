package com.micropower.basic.common.dto.receive;

import lombok.Data;

/**
 * @Date: 2020/9/18 18:05
 * @Description: TODO → 查询后返回的操作记录子记录
 * @Author:Kohaku_川
 **/
@Data
public class OperationChildRecord {
    /**
     * 地址号
     **/
    private Integer address;

    /**
     * 地区号
     **/
    private String areaCode;

    /**
     * 操作时间
     **/
    private String time;

    /**
     * 操作来源
     **/
    private String source;

    /**
     * 操作类型
     **/
    private String type;

    /**
     * 命令类型
     **/
    private String cmdType;
}
