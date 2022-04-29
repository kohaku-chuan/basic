package com.micropower.basic.forward.common212;


import com.micropower.basic.netty.ForwardingServer;
import com.micropower.basic.util.DecoderUtil;

import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class YongKangForward {
    public static void forward(Integer stationId, Integer forwardingAddress, String ip, int port, String flow, String speed, String levelHeight, String totalFlow, String cond) {
        //        液位w05001;流速w05002;瞬时流量w05003;累积流量w05004;电导w01014;氨氮w21003
        String MN = "010571001A003210571" + String.format("%05X", forwardingAddress);
        String head = "##";
        StringBuilder msg = new StringBuilder().append("QN=").append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())).append(";ST=32;CN=2011;PW=").append("123456").append(";MN=")
                .append(MN).append(";Flag=5;CP=&&DataTime=").append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(";w05001-Rtd=").append(levelHeight).append(";w05002-Rtd=")
                .append(speed).append(";w05003-Rtd=").append(flow).append(";w05004-Rtd=").append(totalFlow).append(";w05005-Rtd=").append(cond).append("&&");
        String info = msg.toString();
        String crc = DecoderUtil.calcCrc16(info).toUpperCase();
        crc = crc.length() == 3 ? "0" + crc : crc;
        String writeMsg = head + DecoderUtil.lengthStr(msg.length()) + msg + crc;
        String result = "成功";
        try (Socket socket = new Socket(ip, port); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            byte[] messes = DecoderUtil.hexStr2bytes(DecoderUtil.convertStringToHex(writeMsg) + "0D0A");
            dos.write(Objects.requireNonNull(messes));
            dos.flush();
        } catch (Exception e) {
            result = "失败";
        } finally {
            ForwardingServer.saveRecord(stationId, ip, port, forwardingAddress, "永康212", writeMsg, result);
        }
    }
}
