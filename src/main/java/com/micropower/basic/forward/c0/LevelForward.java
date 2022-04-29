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
 * @description TODO
 * @date 2022/4/26 16:26
 */
public class LevelForward {
    private LevelForward() {

    }
    public static void forward(Integer stationId, String ip, int port, Integer forwardingAddress, String level) {
        String sendMsg = "";
        StringBuilder msg = new StringBuilder();
        String result = "成功";
        try (Socket socket = new Socket(ip, port); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
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
            ForwardingServer.saveRecord(stationId, ip, port, forwardingAddress, "C0液位(超声波)", sendMsg, result);
        }
    }
}
