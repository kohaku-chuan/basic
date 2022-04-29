package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;

/**
 * @Date: 2020/7/13 8:43
 * @Description: TODO →结束通讯报文
 * @Author:Kohaku_川
 **/
public class EndCommunication extends CommonDto {
    @Override
    public Integer getLength() {
        return 9;
    }

    @Override
    public String getCode() {
        return "ce";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        super.encode(out, outStr);
    }
}
