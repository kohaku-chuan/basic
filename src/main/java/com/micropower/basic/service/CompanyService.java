package com.micropower.basic.service;

import com.micropower.basic.entity.DeviceBean;

import java.util.List;
import java.util.Map;

public interface CompanyService {

    /**
     * @return company Map
     * @Author kohaku_C
     * @Description //TODO 通过地区号+设备号获取设备
     * @Date 9:54  2021/3/16
     * @Param 地区号areaNo, 设备号deviceNo
     **/
    DeviceBean getByAddress(String areaCode, Integer address);

    /**
     * @Author kohaku_C
     * @Description //TODO 条件获取设备List
     * @Date  13:37  2021/4/22
     * @Param
     * @return
     **/
    List<DeviceBean> getListByMap(Map<String, Object> map);

    /**
     * @Author kohaku_C
     * @Description //TODO 获取站点转发配置List
     * @Date  13:37  2021/4/22
     * @Param
     * @return
     **/
    List<Map<String, Object>> getStationForward(Integer stationId);

    /**
     * @Author kohaku_C
     * @Description //TODO 通过设备ID获取相关联用户
     * @Date  13:37  2021/4/22
     * @Param
     * @return
     **/
    List<Map<String, Object>> getReceiverList(String id);

    /**
     * @Author kohaku_C
     * @Description //TODO 通过设备ID获取浮标站配置
     * @Date  16:37  2022/3/23
     * @Param
     * @return
     **/
    Map<String,Object> getBuoyStationConfigure(Integer id);

    /**
      * @description TODO 获取接收预警短的信超级管理员用户名单
      * @author Kohaku_川
      * @date 2022/4/24 13:07
      */
    List<Map<String, Object>> getSuperAdminPhone(String type);

    /**
      * @description TODO 通过站点ID获取拥有该站点数据权限的接收预警短信的用户名单
      * @author Kohaku_川
      * @date 2022/4/24 13:14
      */
    List<Map<String, Object>> getNormalUserPhone(String blockUrl,String type);

    /**
      *
      * @description TODO 获取原文转发的站点配置信息
      * @author Kohaku_川
      * @date 2022/4/26 16:42
      */
    List<Map<String, Object>> getOriginForwardConfig(String areaCode, Integer address);

}
