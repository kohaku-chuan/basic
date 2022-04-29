package com.micropower.basic.netty.tcp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.micropower.basic.SpringUtil;
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
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2021/2/9 8:51
 * @Description: TODO →TCP服务
 * @Author:Kohaku_川
 **/
@Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

    private static RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
    private static OperationRecordService operationRecordService = SpringUtil.getBean(OperationRecordService.class);

    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("TCP连接+1");
    }

    /**
     * 客户端发消息会触发
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            //转16进制字符串
            String receiveStr = DecoderUtil.receiveHexToString(bytes);
            CommonDto commonDto = CommonDto.getDecode(receiveStr);
            if (commonDto != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("content", receiveStr);
                map.put("type", "tcp");
                map.put("areaCode", commonDto.getAreaCode());
                map.put("address", commonDto.getAddress());
                map.put("description", JSON.toJSONString(commonDto));
                operationRecordService.insertMessage(map);
                commonDto.setCommunicationMode("tcp");
                ChannelCache.checkChannel(commonDto.getAreaCode() + commonDto.getAddress(), ctx.channel());
                if (commonDto.isFeedback() || Arrays.asList(CommonDto.getQueryBackCode()).contains(commonDto.getCode())) {
                    String address = commonDto.getAreaCode() + commonDto.getAddress();
                    SyncWriteFuture future = (SyncWriteFuture) SyncWriteMap.writeRecords.get(address);
                    if (future != null) {
                        future.setResponse(commonDto);
                    } else {
                        if ("10".equals(commonDto.getCode()) && commonDto.isSuccess()) {
                            redisUtil.hset(commonDto.getAreaCode() + commonDto.getAddress(), "address_set_success", "1", 30000);
                        }
                    }
                } else if (commonDto instanceof RunningStateDto) {
                    ForwardingServer.forwarding(commonDto, receiveStr);
                    redisUtil.llSet("realtimeData", JSON.toJSONString(commonDto, SerializerFeature.IgnoreErrorGetter));
                } else if (commonDto instanceof ValueRecordDto) {
                    ForwardingServer.forwarding(commonDto, receiveStr);
                    redisUtil.llSet("valueRecord", JSON.toJSONString(commonDto, SerializerFeature.IgnoreErrorGetter));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 发生异常触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("TCP连接断开");
    }
}
