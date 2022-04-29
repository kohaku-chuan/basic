package com.micropower.basic.dao;

import com.micropower.basic.entity.StationBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StationDao {

    int networkOffline(@Param("stationId") Integer stationId);

    StationBean getStationByMap(Map<String, Object> map);

    List<StationBean> getStationListByMap(Map<String, Object> map);
}
