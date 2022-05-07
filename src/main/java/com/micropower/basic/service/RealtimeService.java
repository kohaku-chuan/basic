package com.micropower.basic.service;

import com.micropower.basic.entity.FlowRecordBean;
import com.micropower.basic.entity.RealtimeDataBean;

import java.util.List;
import java.util.Map;


public interface RealtimeService {

    /**
     * 条件获取实时记录列表
     */
    List<Map<String, Object>> getListByMap(Map<String, Object> map);

    /**
     * 更新设备网络状态-离线
     */
    boolean deviceOffline(String areaCode, Integer address);

    /**
     * 插入或更新实时数据
     */
    boolean insertUpdateRealtime(RealtimeDataBean realtimeDataBean);

    /**
     * 批量插入状态记录
     */
    boolean insertStateRecordList(List<Map<String, Object>> stateRecordList);

    /**
     * 插入状态记录
     */
    boolean insertStateRecord(RealtimeDataBean realtimeDataBean);

    /**
     * 批量插入水质记录
     */
    boolean insertValueRecordList(List<Map<String, Object>> valueRecordList);

    /**
     * 插入水质记录
     */
    boolean insertValueRecord(Map<String, Object> runMap);

    /**
     * 添加周期记录
     */
    boolean insertCycleRecord(Map<String, Object> map);

    /**
     * 获取流量基础值和时间
     */
    Map<String, Object> getTotalFlowBasic(int stationId);

    /**
     * 批量添加流量分钟记录
     */
    boolean insertFlowMinRecordList(List<Map<String, Object>> valueRecordList);

    /**
     * 添加流量分钟记录
     */
    boolean insertFlowMinRecord(FlowRecordBean recordBean);

    /**
     * 添加流量小时记录即日统计
     */
    boolean insertFlowHourRecord(FlowRecordBean recordBean);

    /**
     * 添加流量日记录即月统计
     */
    boolean insertFlowDayRecord(FlowRecordBean recordBean);

    /**
     * 插入水质记录分钟记录
     */
    boolean insertValueMinRecord(Map<String, Object> minMap);

    /**
     * 获取全局预警参数
     */
    Map<String, String> getGlobalWarn();

    /**
     * 获取上次雨量脉冲值
     */
    Map<String, Object> getPulseBasic(Integer stationId);

    /**
     * 插入雨量记录
     */
    boolean insertRainfallMinRecord(RealtimeDataBean realtimeDataBean);

    /**
     * 更新雨量基本值
     */
    boolean updateFlowBase(Integer stationId, String totalFlow);

    /**
     * 获取上一次通讯时间
     */
    String getLastTimeUploadTime(Integer stationId);

    Map<String, Object> getRealTimeById(int id);

}
