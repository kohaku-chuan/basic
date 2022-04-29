package com.micropower.basic.dao;

import com.micropower.basic.common.dto.receive.ExceptionChildRecord;
import com.micropower.basic.common.dto.receive.OperationChildRecord;
import com.micropower.basic.common.dto.receive.QuerySettingBackDto;
import com.micropower.basic.entity.ExceptionRecordBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OperationRecordDao {

    int insertExceptionRecord(ExceptionRecordBean exceptionRecordBean);

    int insertMessage(Map<String, Object> map);

    int cleanUpMessage();

    int insertOperationRecord(Map<String, Object> operationMap);

    int updateOperationRecord(@Param("id") String id, @Param("feedback") String feedback);

    int insertCycleRecord(Map<String, Object> map);

    int insertHistoryRecord(Map<String, Object> map);

    int insertDeviceOperationRecord(OperationChildRecord record);

    int insertDeviceExceptionRecord(ExceptionChildRecord record);

    int insertUpdateDeviceConfig(QuerySettingBackDto data);

    int insertForwardRecord(Map<String, Object> map);

    int cleanUpForward();

    Map<String, Object> getMaxFlow(Map<String, Object> param);

    Map<String, Object> getFlowAvg(Map<String, Object> param);

    int insertMessageRecord(Map<String, Object> recordMap);

    List<Map<String, Object>> getWaterList(Map<String, Object> param);
}
