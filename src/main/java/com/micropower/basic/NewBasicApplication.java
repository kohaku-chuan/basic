package com.micropower.basic;

import com.micropower.basic.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.micropower.basic.dao")
public class NewBasicApplication implements CommandLineRunner {
    @Value("${netty.ip}")
    private String ip;
    @Value("${netty.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(NewBasicApplication.class);
        //添加AEP监听
        springApplication.run(args);
    }

    @Override
    public void run(String... args) {
        //启动Netty服务端
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new InetSocketAddress(ip, port));
    }
}
