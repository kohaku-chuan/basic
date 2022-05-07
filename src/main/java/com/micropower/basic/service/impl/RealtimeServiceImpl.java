package com.micropower.basic.service.impl;

import com.micropower.basic.dao.RealtimeDao;
import com.micropower.basic.entity.FlowRecordBean;
import com.micropower.basic.entity.RealtimeDataBean;
import com.micropower.basic.service.RealtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Date: 2021/3/16 10:08
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
@Service
public class RealtimeServiceImpl implements RealtimeService {
    private final
    RealtimeDao realtimeDao;

    @Autowired
    public RealtimeServiceImpl(RealtimeDao realtimeDao) {
        this.realtimeDao = realtimeDao;
    }

    @Override
    public List<Map<String, Object>> getListByMap(Map<String, Object> map) {
        return realtimeDao.getListByMap(map);
    }

    @Override
    public boolean deviceOffline(String areaCode, Integer address) {
        return realtimeDao.deviceOffline(areaCode, address) > 0;
    }

    @Override
    public boolean insertUpdateRealtime(RealtimeDataBean realtimeDataBean) {
        return realtimeDao.insertUpdateRealtime(realtimeDataBean) > 0;
    }

    @Override
    public boolean insertStateRecordList(List<Map<String, Object>> stateRecordList) {
        return realtimeDao.insertStateRecordList(stateRecordList) > 0;
    }

    @Override
    public boolean insertStateRecord(RealtimeDataBean realtimeDataBean) {
        return realtimeDao.insertStateRecord(realtimeDataBean) > 0;
    }

    @Override
    public boolean insertValueRecordList(List<Map<String, Object>> valueRecordList) {
        return realtimeDao.insertValueRecordList(valueRecordList) > 0;
    }

    @Override
    public boolean insertValueRecord(Map<String, Object> runMap) {
        return realtimeDao.insertValueRecord(runMap) > 0;
    }

    @Override
    public boolean insertCycleRecord(Map<String, Object> map) {
        return realtimeDao.insertCycleRecord(map) > 0;
    }

    @Override
    public Map<String, Object> getTotalFlowBasic(int stationId) {
        return realtimeDao.getTotalFlowBasic(stationId);
    }

    @Override
    public boolean insertFlowMinRecordList(List<Map<String, Object>> valueRecordList) {
        return realtimeDao.insertFlowMinRecordList(valueRecordList) > 0;
    }

    @Override
    public boolean insertFlowMinRecord(FlowRecordBean recordBean) {
        return realtimeDao.insertFlowMinRecord(recordBean) > 0;
    }

    @Override
    public boolean insertFlowHourRecord(FlowRecordBean recordBean) {
        return realtimeDao.insertFlowHourRecord(recordBean) > 0;
    }

    @Override
    public boolean insertFlowDayRecord(FlowRecordBean recordBean) {
        return realtimeDao.insertFlowDayRecord(recordBean) > 0;
    }

    @Override
    public boolean insertValueMinRecord(Map<String, Object> minMap) {
        return realtimeDao.insertValueMinRecord(minMap) > 0;
    }

    @Override
    public Map<String, String> getGlobalWarn() {
        return realtimeDao.getGlobalWarn();
    }

    @Override
    public Map<String, Object> getPulseBasic(Integer stationId) {
        return realtimeDao.getPulseBasic(stationId);
    }

    @Override
    public boolean insertRainfallMinRecord(RealtimeDataBean realtimeDataBean) {
        return realtimeDao.insertRainfallMinRecord(realtimeDataBean) > 0;
    }

    @Override
    public boolean updateFlowBase(Integer stationId, String totalFlow) {
        return realtimeDao.updateFlowBase(stationId, totalFlow) > 0;
    }

    @Override
    public String getLastTimeUploadTime(Integer stationId) {
        return realtimeDao.getLastTimeUploadTime(stationId);
    }

    @Override
    public Map<String, Object> getRealTimeById(int id) {
        return realtimeDao.getRealTimeById(id);
    }
}
