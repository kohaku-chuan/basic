package com.micropower.basic.service;

import com.micropower.basic.entity.StationBean;

import java.util.List;
import java.util.Map;

public interface StationService {
    /**
      * @description TODO 站点离线
      * @author Kohaku_川
      * @date 2022/4/24 10:12
      */
    boolean networkOffline(Integer stationId);

    /**
      * @description TODO 条件获取站点
      * @author Kohaku_川
      * @date 2022/4/24 10:21
      */
    StationBean getStationByMap(Map<String,Object> map);

    /**
     * @description TODO 获取流量监测站点
     * @author Kohaku_川
     * @date 2022/4/24 10:21
     */
    List<StationBean> getFlowStationList();
}
