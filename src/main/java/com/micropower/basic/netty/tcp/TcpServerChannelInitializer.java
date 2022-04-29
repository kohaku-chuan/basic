package com.micropower.basic.netty.tcp;

import com.micropower.basic.netty.CommonEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Date: 2021/2/9 8:50
 * @Description: TODO → TCP服务初始化
 * @Author:Kohaku_川
 **/
public class TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new TcpServerHandler());
        socketChannel.pipeline().addLast(new CommonEncoder());
    }
}
