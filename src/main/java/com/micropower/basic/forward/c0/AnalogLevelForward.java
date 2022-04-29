package com.micropower.basic.forward.c0;

import com.micropower.basic.netty.ForwardingServer;
import com.micropower.basic.util.CommonUtil;
import com.micropower.basic.util.DecoderUtil;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kohaku_川
 * @description
 * @date 2022/3/31 13:49
 */
public class AnalogLevelForward {

    private AnalogLevelForward() {

    }

    public static void forward(Integer stationId, String ip, int port, Integer forwardingAddress, List<Map<String, Object>> list) {
        String sendMsg = "";
        StringBuilder msg = new StringBuilder();
        String result = "成功";
        String level = "0";
        try (Socket socket = new Socket(ip, port); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            for (Map<String, Object> map : list) {
                if ("液位".equals(map.get("name").toString())) {
                    level = map.get("value").toString();
                    level = "异常".equals(level) ? "-10" : level;
                }
            }
            String c0Address = String.format("%04X", forwardingAddress).toUpperCase();
            String c0Level = Objects.requireNonNull(DecoderUtil.receiveHexToString(DecoderUtil.float2byte(Float.parseFloat(level)))).toUpperCase();
            msg.append("14C0023B").append(c0Address).append("0120").append(CommonUtil.parseBack1032(c0Level));
            msg = DecoderUtil.getDate(new Date(), msg, 6);
            sendMsg = "AA" + msg.append(DecoderUtil.getModbusCRC(msg.toString())) + "A5";
            byte[] messes = DecoderUtil.hexStr2bytes(sendMsg);
            dos.write(Objects.requireNonNull(messes));
            dos.flush();
        } catch (Exception e) {
            result = "失败";
        } finally {
            ForwardingServer.saveRecord(stationId, ip, port, forwardingAddress, "C0液位(模拟通道)", sendMsg, result);
        }
    }
}
