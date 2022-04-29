package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;

/**
 * @Date: 2020/7/9 11:33
 * @Description: TODO →查询参数设置
 * @Author:Kohaku_川
 **/
public class QuerySettingDto extends CommonDto {

    @Override
    public Integer getLength() {
        return 11;
    }

    @Override
    public String getCode() {
        return "cc";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //起始参编号数
        outStr.append("01")
                //查询个数,共131个,读取到125
                .append("7D");
        super.encode(out, outStr);
    }
}
