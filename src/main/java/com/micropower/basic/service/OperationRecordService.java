package com.micropower.basic.service;


import com.micropower.basic.common.dto.receive.ExceptionChildRecord;
import com.micropower.basic.common.dto.receive.OperationChildRecord;
import com.micropower.basic.common.dto.receive.QuerySettingBackDto;
import com.micropower.basic.entity.ExceptionRecordBean;

import java.util.List;
import java.util.Map;

public interface OperationRecordService {

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加一条指令操作记录
     * @Date 17:55  2021/3/16
     * @Param
     **/
    boolean insertOperationRecord(Map<String, Object> operationMap);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 更新一条指令操作记录
     * @Date 17:55  2021/3/16
     * @Param
     **/
    boolean updateOperationRecord(String id, String feedback);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加一条异常记录
     * @Date 10:28  2021/3/16
     * @Param
     **/
    boolean insertExceptionRecord(ExceptionRecordBean recordBean);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加一条原始报文记录
     * @Date 11:11  2021/3/16
     * @Param
     **/
    boolean insertMessage(Map<String, Object> map);


    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 清除过期报文
     * @Date 13:24  2021/3/16
     * @Param
     **/
    void cleanUpMessage();

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加周期历史记录
     * @Date 9:14  2021/3/17
     * @Param
     **/
    void insertCycleRecord(Map<String, Object> map);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加查询所得历史记录
     * @Date 9:14  2021/3/17
     * @Param
     **/
    void insertHistoryRecordList(List<Map<String, Object>> valueList);


    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 批量添加设备查询所得操作记录
     * @Date 10:26  2021/3/17
     * @Param
     **/
    void insertDeviceOperationRecordList(List<OperationChildRecord> operationChildRecordList);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 批量添加设备查询所得异常记录
     * @Date 10:44  2021/3/17
     * @Param
     **/
    void insertExceptionRecordList(List<ExceptionChildRecord> exceptionChildRecords);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 插入更新查询所得所有参数
     * @Date 10:44  2021/3/17
     * @Param
     **/
    boolean insertUpdateDeviceConfig(QuerySettingBackDto data);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 添加转发记录
     * @Date 15:56  2021/3/17
     * @Param
     **/
    boolean insertForwardRecord(Map<String, Object> map);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 获取最大流量值
     * @Date 13:56  2021/4/22
     * @Param
     **/
    Map<String, Object> getMaxFlow(Map<String, Object> param);

    /**
     * @return
     * @Author kohaku_C
     * @Description //TODO 获取液位，流速，瞬时流量平均值
     * @Date 13:56  2021/4/22
     * @Param
     **/
    Map<String, Object> getFlowAvg(Map<String, Object> param);

    /**
     * @description TODO 保存预警短信发送记录
     * @author Kohaku_川
     * @date 2022/4/24 13:58
     */
    boolean insertMessageRecord(Map<String, Object> recordMap);

    List<Map<String, Object>> getWaterList(Map<String, Object> param);

}
