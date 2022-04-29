package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;

/**
 * @Date: 2020/7/9 11:33
 * @Description: TODO →反馈周期数据接收成功
 * @Author:Kohaku_川
 **/
public class CycleFeedbackDto extends CommonDto {

    @Override
    public Integer getLength() {
        return 10;
    }

    @Override
    public String getCode() {
        return "D1";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //成功
        outStr.append("01");
        super.encode(out, outStr);
    }
}
