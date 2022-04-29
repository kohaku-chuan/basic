package com.micropower.basic.forward.c0;

import com.micropower.basic.entity.FlowForwardBean;
import com.micropower.basic.netty.ForwardingServer;
import com.micropower.basic.util.CommonUtil;
import com.micropower.basic.util.DecoderUtil;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
/**
  * TODO 小桥新C0协议转发流量数据
  * @author Kohaku_川
  * @date 2022/4/26 15:54
  */
public class FlowForwardC0 {
    private FlowForwardC0() {
    }

    public static void forward(FlowForwardBean bean) {
        //C0协议因子编码，瞬时流量0，累积流量24（18），液位32（20），流速80（50），4个因子共15+4*5=35（23）
        String sendMsg = "";
        StringBuilder msg = new StringBuilder();
        String result = "成功";
        try (Socket socket = new Socket(bean.getIp(), bean.getPort()); DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            String c0Address = String.format("%04X", bean.getForwardingAddress()).toUpperCase();
            String c0Level = Objects.requireNonNull(DecoderUtil.receiveHexToString(DecoderUtil.float2byte(Float.parseFloat(bean.getLevelHeight())))).toUpperCase();
            String c0Speed = Objects.requireNonNull(DecoderUtil.receiveHexToString(DecoderUtil.float2byte(Float.parseFloat(bean.getSpeed())))).toUpperCase();
            String c0Flow = Objects.requireNonNull(DecoderUtil.receiveHexToString(DecoderUtil.float2byte(Float.parseFloat(bean.getFlow())))).toUpperCase();
            String c0totalFlow = Objects.requireNonNull(DecoderUtil.receiveHexToString(DecoderUtil.float2byte(Float.parseFloat(bean.getTotalFlow())))).toUpperCase();
            msg.append("23C0023B").append(c0Address).append("0400").append(CommonUtil.parseBack1032(c0Flow)).append("18").append(CommonUtil.parseBack1032(c0totalFlow)).append("20").append(CommonUtil.parseBack1032(c0Level)).append("50").append(CommonUtil.parseBack1032(c0Speed));
            msg = DecoderUtil.getDate(new Date(), msg, 6);
            sendMsg = "AA" + msg.append(DecoderUtil.getModbusCRC(msg.toString())) + "A5";
            byte[] messes = DecoderUtil.hexStr2bytes(sendMsg);
            dos.write(Objects.requireNonNull(messes));
            dos.flush();
        } catch (Exception e) {
            result = "失败";
        } finally {
            ForwardingServer.saveRecord(bean.getStationId(), bean.getIp(), bean.getPort(), bean.getForwardingAddress(), "C0流量", sendMsg, result);
        }
    }

}
