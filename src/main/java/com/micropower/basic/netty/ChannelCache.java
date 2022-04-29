package com.micropower.basic.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2021/2/9 13:54
 * @Description: TODO → Channel储存管理
 * @Author:Kohaku_川
 **/

public class ChannelCache {

    /**
     * 存储所有Channel
     */
//    private ChannelGroup channelGroup = new DefaultChannelGroup("channelGroups", GlobalEventExecutor.INSTANCE);

    /**
     * 存储所有TCP-Channel,K:区域码+地址号，V:channel
     */
    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void checkChannel(String key, Channel channel) {
        channelMap.put(key, channel);
    }

    public static Channel getChannel(String key) {
        return channelMap.get(key);
    }

    /**
     * 存储所有UDP-DatagramPacket,K:区域码+地址号，V:datagramPacket
     */
    private static Map<String, DatagramPacket> packetMap = new ConcurrentHashMap<>();

    public static void checkPacket(String key, DatagramPacket packet) {
        packetMap.put(key, packet);
    }

    public static DatagramPacket getPacket(String key) {
        return packetMap.get(key);
    }

    private static Map<String, ChannelHandlerContext> contextMap = new ConcurrentHashMap<>();

    public static void putContext(String key, ChannelHandlerContext channelHandlerContext) {
        contextMap.put(key, channelHandlerContext);
    }

    public static ChannelHandlerContext getContext(String key) {
        return contextMap.get(key);
    }
}
