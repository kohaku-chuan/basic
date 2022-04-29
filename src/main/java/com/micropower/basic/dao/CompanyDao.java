package com.micropower.basic.dao;

import com.micropower.basic.entity.DeviceBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CompanyDao {

    DeviceBean getByAddress(@Param("areaCode") String areaCode, @Param("address") Integer address);

    List<DeviceBean> getListByMap(Map<String,Object> map);

    List<Map<String, Object>> getStationForward(@Param("stationId")Integer stationId);

    List<Map<String, Object>> getReceiverList(String id);

    Map<String, Object> getBuoyStationConfigure(Integer id);

    List<Map<String, Object>> getSuperAdminPhone(String type);

    List<Map<String, Object>> getNormalUserPhone(@Param("blockUrl") String blockUrl,@Param("type") String type);

    List<Map<String, Object>> getOriginForwardConfig(@Param("areaCode")String areaCode, @Param("address")Integer address);

}
