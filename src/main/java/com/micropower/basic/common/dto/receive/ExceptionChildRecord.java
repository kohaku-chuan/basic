package com.micropower.basic.common.dto.receive;

import lombok.Data;

/**
 * @Date: 2020/9/19 14:21
 * @Description: TODO →查询后返回的异常记录子记录
 * @Author:Kohaku_川
 **/
@Data
public class ExceptionChildRecord {
    /**
     * 地址号
     **/
    private Integer deviceNo;

    /**
     * 地区号
     **/
    private String areaNo;

    /**
     * 操作时间
     **/
    private String time;

    /**
     * 异常类型
     **/
    private String type;
}
