package com.micropower.basic.netty;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.service.CompanyService;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.util.DecoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * @Date: 2021/3/17 14:14
 * @Description: 原文报文转发
 * @Author:Kohaku_川
 **/
@Component
public class ForwardingServer {
    private static ForwardingServer server;

    private ForwardingServer() {
    }

    private @Autowired
    OperationRecordService operationRecordService;
    private @Autowired
    CompanyService companyService;

    @PostConstruct
    public void init() {
        server = this;
        server.operationRecordService = this.operationRecordService;
        server.companyService = this.companyService;
    }

    public static void forwarding(CommonDto dto, String msg) {
        List<Map<String, Object>> configureList = server.companyService.getOriginForwardConfig(dto.getAreaCode(), dto.getAddress());
        if (!configureList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : configureList) {
                String ip = stringObjectMap.get("ip").toString();
                int port = Integer.parseInt(stringObjectMap.get("port").toString());
                int stationId = Integer.parseInt(stringObjectMap.get("station_id").toString());
                originalForwarding(stationId, ip, port, msg);
            }
        }
    }

    private static void originalForwarding(int stationId, String ip, int port, String msg) {
        String result = "成功";
        try (Socket socket = new Socket(ip, port); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            byte[] messes = DecoderUtil.hexStr2bytes(msg);
            dos.write(Objects.requireNonNull(messes));
            dos.flush();
        } catch (Exception e) {
            result = "失败";
        } finally {
            saveRecord(stationId, ip, port, 0, "原始报文", msg, result);
        }
    }

    public static void saveRecord(Integer stationId, String ip, int port, Integer forwardingAddress, String type, String content, String result) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("stationId", stationId);
        map.put("ip", ip);
        map.put("port", port);
        map.put("forwardingAddress", forwardingAddress);
        map.put("type", type);
        map.put("content", content);
        map.put("result", result);
        server.operationRecordService.insertForwardRecord(map);
    }

}
