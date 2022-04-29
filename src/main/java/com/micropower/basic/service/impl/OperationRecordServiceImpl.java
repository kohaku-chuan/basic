package com.micropower.basic.service.impl;

import com.micropower.basic.common.dto.receive.ExceptionChildRecord;
import com.micropower.basic.common.dto.receive.HistoryChildRecord;
import com.micropower.basic.common.dto.receive.OperationChildRecord;
import com.micropower.basic.common.dto.receive.QuerySettingBackDto;
import com.micropower.basic.dao.CompanyDao;
import com.micropower.basic.dao.OperationRecordDao;
import com.micropower.basic.entity.ExceptionRecordBean;
import com.micropower.basic.service.OperationRecordService;
import org.apache.pulsar.shade.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Date: 2021/2/19 11:21
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
@Service
public class OperationRecordServiceImpl implements OperationRecordService {

    private final
    CompanyDao companyDao;
    private final
    OperationRecordDao operationRecordDao;

    @Autowired
    public OperationRecordServiceImpl(CompanyDao companyDao, OperationRecordDao operationRecordDao) {
        this.companyDao = companyDao;
        this.operationRecordDao = operationRecordDao;
    }

    @Override
    public boolean insertOperationRecord(Map<String, Object> operationMap) {
        return operationRecordDao.insertOperationRecord(operationMap) > 0;
    }

    @Override
    public boolean updateOperationRecord(String id, String feedback) {
        return operationRecordDao.updateOperationRecord(id, feedback) > 0;
    }

    @Override
    public boolean insertExceptionRecord(ExceptionRecordBean recordBean) {
        return operationRecordDao.insertExceptionRecord(recordBean) > 0;
    }

    @Override
    public boolean insertMessage(Map<String, Object> map) {
        return operationRecordDao.insertMessage(map) > 0;
    }

    @Override
    public void cleanUpMessage() {
        operationRecordDao.cleanUpMessage();
        operationRecordDao.cleanUpForward();
    }

    @Override
    public void insertCycleRecordList(List<Map<String, Object>> valueList) {
        if (!valueList.isEmpty()) {
            for (Map<String, Object> map : valueList) {
                try {
                    operationRecordDao.insertCycleRecord(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void insertHistoryRecordList(List<Map<String, Object>> valueList) {
        if (!valueList.isEmpty()) {
            for (Map<String, Object> record : valueList) {
                operationRecordDao.insertHistoryRecord(record);
            }
        }
    }

    @Override
    public void insertDeviceOperationRecordList(List<OperationChildRecord> operationChildRecordList) {
        if (!operationChildRecordList.isEmpty()) {
            for (OperationChildRecord record : operationChildRecordList) {
                operationRecordDao.insertDeviceOperationRecord(record);
            }
        }
    }

    @Override
    public void insertExceptionRecordList(List<ExceptionChildRecord> exceptionChildRecords) {
        if (!exceptionChildRecords.isEmpty()) {
            for (ExceptionChildRecord record : exceptionChildRecords) {
                operationRecordDao.insertDeviceExceptionRecord(record);
            }
        }
    }

    @Override
    public boolean insertUpdateDeviceConfig(QuerySettingBackDto data) {
        return operationRecordDao.insertUpdateDeviceConfig(data) > 0;
    }

    @Override
    public boolean insertForwardRecord(Map<String, Object> map) {
        return operationRecordDao.insertForwardRecord(map) > 0;
    }

    @Override
    public Map<String, Object> getMaxFlow(Map<String, Object> param) {
        return operationRecordDao.getMaxFlow(param);
    }

    @Override
    public Map<String, Object> getFlowAvg(Map<String, Object> param) {
        return operationRecordDao.getFlowAvg(param);
    }

    @Override
    public boolean insertMessageRecord(Map<String, Object> recordMap) {
        return operationRecordDao.insertMessageRecord(recordMap) > 0;
    }

    @Override
    public List<Map<String, Object>> getWaterList(Map<String, Object> param) {
        return operationRecordDao.getWaterList(param);
    }

}
