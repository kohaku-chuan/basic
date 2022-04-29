package com.micropower.basic.netty;


import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author kohaku_C
 * @Description //TODO → 自定义编码器
 * @Date  10:35  2021/2/20
 * @Param
 * @return
 **/
public class CommonEncoder  extends MessageToByteEncoder<Object>  {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object dto, ByteBuf byteBuf) throws Exception {
        if(dto instanceof CommonDto){
            CommonDto dta=(CommonDto) dto;
            dta.getEncode(byteBuf);
        }
    }
}
