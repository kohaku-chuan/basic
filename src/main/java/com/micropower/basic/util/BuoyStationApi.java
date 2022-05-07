package com.micropower.basic.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kohaku_川
 * @description TODO
 * @date 2022/3/30 18:04
 */
@Data
public class BuoyStationApi {
    private static final String ROBOT_API_HOST = "http://localhost:6044/buoyStationApi/writeToBuoyStation";
    private double cod;
    private double ph;
    private double ss;
    private double nh3;
    private String address;

    public BuoyStationApi(String address, double cod, double ph, double ss, double nh3) {
        super();
        this.address = address;
        this.cod = cod;
        this.ph = ph;
        this.ss = ss;
        this.nh3 = nh3;
    }

    private String buildParams() {
        Map<String, Object> param = new HashMap<>(5);
        param.put("address", address);
        param.put("cod", cod);
        param.put("ph", ph);
        param.put("ss", ss);
        param.put("nh3", nh3);
        return JSON.toJSONString(param);
    }

    public String getRobotResult() {
        return HttpPost.doPost(ROBOT_API_HOST, buildParams());
    }
}

