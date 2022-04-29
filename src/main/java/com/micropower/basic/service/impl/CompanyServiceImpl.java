package com.micropower.basic.service.impl;

import com.micropower.basic.dao.CompanyDao;
import com.micropower.basic.entity.DeviceBean;
import com.micropower.basic.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Date: 2021/3/16 9:57
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    public CompanyDao companyDao;

    @Override
    public DeviceBean getByAddress(String areaCode, Integer address) {
        return this.companyDao.getByAddress(areaCode, address);
    }

    @Override
    public List<DeviceBean> getListByMap(Map<String, Object> map) {
        return companyDao.getListByMap(map);
    }

    @Override
    public List<Map<String, Object>> getStationForward(Integer stationId) {
        return companyDao.getStationForward(stationId);
    }

    @Override
    public List<Map<String, Object>> getReceiverList(String id) {
        return companyDao.getReceiverList(id);
    }

    @Override
    public Map<String, Object> getBuoyStationConfigure(Integer id) {
        return companyDao.getBuoyStationConfigure(id);
    }

    @Override
    public List<Map<String, Object>> getSuperAdminPhone(String type) {
        return companyDao.getSuperAdminPhone(type);
    }

    @Override
    public List<Map<String, Object>> getNormalUserPhone(String blockUrl, String type) {
        return companyDao.getNormalUserPhone(blockUrl, type);
    }

    @Override
    public List<Map<String, Object>> getOriginForwardConfig(String areaCode, Integer address) {
        return companyDao.getOriginForwardConfig(areaCode, address);
    }

}
