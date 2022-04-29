package com.micropower.basic.netty.UDP;

import com.micropower.basic.netty.CommonEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

/**
 * @Date: 2021/2/9 8:50
 * @Description: TODO → UDP服务初始化
 * @Author:Kohaku_川
 **/
public class UdpServerChannelInitializer extends ChannelInitializer<DatagramChannel> {
    @Override
    protected void initChannel(DatagramChannel datagramChannel) {
        datagramChannel.pipeline().addLast(new UdpServerHandler());
        datagramChannel.pipeline().addLast(new CommonEncoder());
    }
}
