package com.micropower.basic.forward.common212;

import com.micropower.basic.netty.ForwardingServer;
import com.micropower.basic.util.DecoderUtil;

import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FlowForward {
    public static void forward(Integer stationId, Integer forwardingAddress, String ip, int port, String flow, String speed, String levelHeight, String totalFlow, String tiltAngle, String voltage, String sensorVoltage, String temp) {
//        液位w05001;流速w05002;瞬时流量w05003;累积流量w05004;倾角w05005;电压w05006;温度w05007;传感器电压w05008
        String MN = "0571010203040506070" + String.format("%05d", forwardingAddress);
        String head = "##";
        StringBuilder msg = new StringBuilder().append("QN=").append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())).append(";ST=32;CN=2011;PW=").append("123456").append(";MN=")
                .append(MN).append(";Flag=5;CP=&&DataTime=").append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append(";w05001-Rtd=" + levelHeight).append(";w05002-Rtd=").append(speed).
                        append(";w05003-Rtd=").append(flow).append(";w05004-Rtd=").append(totalFlow).append(";w05005-Rtd=").append(tiltAngle).append(";w05006-Rtd=").append(voltage).
                        append(";w05007-Rtd=" + temp).append(";w05008-Rtd=" + sensorVoltage).append("&&");
        String info = msg.toString();
        String crc = DecoderUtil.calcCrc16(info).toUpperCase();
        crc = crc.length() == 3 ? "0" + crc : crc;
        String writeMsg = head + DecoderUtil.lengthStr(msg.length()) + msg + crc;
        String result = "成功";
        try (Socket socket = new Socket(ip, port); DataOutputStream dos = new DataOutputStream(socket.getOutputStream());) {
            byte[] messes = DecoderUtil.hexStr2bytes(DecoderUtil.convertStringToHex(writeMsg) + "0D0A");
            dos.write(Objects.requireNonNull(messes));
            dos.flush();
        } catch (Exception e) {
            result = "失败";
        } finally {
            ForwardingServer.saveRecord(stationId, ip, port, forwardingAddress, "212流量", writeMsg, result);
        }
    }
}
