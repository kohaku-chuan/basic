package com.micropower.basic.service.impl;

import com.micropower.basic.dao.StationDao;
import com.micropower.basic.entity.StationBean;
import com.micropower.basic.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/4/24 10:11
 */
@Service
public class StationServiceImpl implements StationService {
    @Autowired
    StationDao dao;

    @Override
    public boolean networkOffline(Integer stationId) {
        return dao.networkOffline(stationId) > 0;
    }

    @Override
    public StationBean getStationByMap(Map<String, Object> map) {
        return dao.getStationByMap(map);
    }

    @Override
    public List<StationBean> getStationListByMap(Map<String, Object> map) {
        return dao.getStationListByMap(map);
    }
}
