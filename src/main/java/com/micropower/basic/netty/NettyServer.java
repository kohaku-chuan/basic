package com.micropower.basic.netty;

import com.micropower.basic.netty.tcp.TcpServerChannelInitializer;
import com.micropower.basic.netty.UDP.UdpServerChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;


import java.net.InetSocketAddress;

/**
 * @Date: 2021/2/9 10:41
 * @Description: TODO → Netty 服务初始化
 * @Author:Kohaku_川
 **/
@Slf4j
public class NettyServer {

    public void start(InetSocketAddress socketAddress) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //TCP设置
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childHandler(new TcpServerChannelInitializer());
            Channel channel1 = bootstrap.bind(socketAddress.getPort()).sync().channel();
            log.info("TCP服务启动");

            //UDP设置
            Bootstrap bootstrap2 = new Bootstrap();
            bootstrap2.group(workerGroup);
            bootstrap2.channel(NioDatagramChannel.class);
            bootstrap2.option(ChannelOption.SO_BROADCAST, true);
            bootstrap2.option(ChannelOption.SO_RCVBUF, 1024 * 1024);
            bootstrap2.option(ChannelOption.SO_SNDBUF, 1024 * 1024);
            bootstrap2.handler(new UdpServerChannelInitializer());
            Channel channel2 = bootstrap2.bind(socketAddress.getPort()).sync().channel();
            log.info("UDP服务启动");

            channel1.closeFuture().sync();
            channel2.closeFuture().await();
        } catch (Exception e) {
            log.error("Netty服务异常---------" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
