package com.micropower.basic.netty.UDP;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.common.dto.receive.RunningStateDto;
import com.micropower.basic.common.dto.receive.ValueRecordDto;
import com.micropower.basic.netty.ChannelCache;
import com.micropower.basic.netty.ForwardingServer;
import com.micropower.basic.netty.syncWrite.SyncWriteFuture;
import com.micropower.basic.netty.syncWrite.SyncWriteMap;
import com.micropower.basic.service.OperationRecordService;
import com.micropower.basic.util.DecoderUtil;
import com.micropower.basic.util.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2021/2/9 8:51
 * @Description: TODO → UDP服务
 * @Author:Kohaku_川
 **/
@Slf4j
@Component
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static UdpServerHandler udpServerHandler;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OperationRecordService operationRecordService;

    @PostConstruct
    public void init() {
        udpServerHandler = this;
        udpServerHandler.redisUtil = this.redisUtil;
        udpServerHandler.operationRecordService = this.operationRecordService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("UDP通道已经连接");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
        ByteBuf msg = datagramPacket.content();
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        String receiveStr = DecoderUtil.receiveHexToString(bytes);
        CommonDto commonDto = null;
        if ((receiveStr) != null) {
            commonDto = CommonDto.getDecode(receiveStr);
        }
        if (commonDto != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("time", new Date());
            map.put("content", receiveStr);
            map.put("type", "UDP");
            map.put("areaCode", commonDto.getAreaCode());
            map.put("address", commonDto.getAddress());
            map.put("description", JSONObject.toJSONString(commonDto));
            udpServerHandler.operationRecordService.insertMessage(map);
            commonDto.setCommunicationMode("UDP");
            ChannelCache.checkPacket(commonDto.getAreaCode() + commonDto.getAddress(), datagramPacket);
            ChannelCache.putContext(commonDto.getAreaCode() + commonDto.getAddress(), channelHandlerContext);
            if (commonDto.isFeedback() || Arrays.asList(CommonDto.getQueryBackCode()).contains(commonDto.getCode())) {
                String address = commonDto.getAreaCode() + commonDto.getAddress();
                SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.writeRecords.get(address);
                if (future != null) {
                    future.setResponse(commonDto);
                } else {
                    if ("10".equals(commonDto.getCode()) && commonDto.isSuccess()) {
                        udpServerHandler.redisUtil.hset(commonDto.getAreaCode() + commonDto.getAddress(), "address_set_success", "1", 30000);
                    }
                }
            } else if (commonDto instanceof RunningStateDto) {
                ForwardingServer.forwarding(commonDto, receiveStr);
                udpServerHandler.redisUtil.lSet("realtimeData", JSONObject.toJSONString(commonDto, SerializerFeature.IgnoreErrorGetter));
            } else if (commonDto instanceof ValueRecordDto) {
                ForwardingServer.forwarding(commonDto, receiveStr);
                udpServerHandler.redisUtil.llSet("valueRecord", JSONObject.toJSONString(commonDto, SerializerFeature.IgnoreErrorGetter));
            }
        }
    }
}
