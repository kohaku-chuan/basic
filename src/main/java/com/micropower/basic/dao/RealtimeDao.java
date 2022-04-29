package com.micropower.basic.dao;

import com.micropower.basic.entity.RealtimeDataBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RealtimeDao {

    List<Map<String, Object>> getListByMap(Map<String, Object> map);

    int deviceOffline(@Param("areaCode") String areaCode, @Param("address") Integer address);

    int insertUpdateRealtime(RealtimeDataBean realtimeDataBean);

    int insertStateRecordList(@Param("list") List<Map<String, Object>> stateRecordList);

    int insertValueRecordList(@Param("list") List<Map<String, Object>> valueRecordList);

    int insertCycleRecord(Map<String, Object> map);

    Map<String, Object> getTotalFlowBasic(@Param("stationId") int stationId);

    int insertFlowMinRecordList(@Param("list") List<Map<String, Object>> valueRecordList);

    int insertFlowHourRecordList(@Param("list") List<Map<String, Object>> valueRecordList);

    int insertFlowDayRecordList(@Param("list") List<Map<String, Object>> valueRecordList);

    int insertValueMinRecord(Map<String, Object> minMap);

    Map<String, String> getGlobalWarn();

    int insertStateRecord(RealtimeDataBean realtimeDataBean);

    int insertValueRecord(Map<String, Object> runMap);

    int insertFlowMinRecord(Map<String, Object> runMap);

    Map<String, Object> getPulseBasic(@Param("stationId") Integer stationId);

    int insertRainfallMinRecord(Map<String, Object> runMap);

    int updateFlowBase(@Param("stationId") Integer stationId, @Param("totalFlow") String totalFlow);

    String getLastTimeUploadTime(@Param("stationId") Integer stationId);

}
