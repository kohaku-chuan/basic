package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;

public class RunningStatuBackDto extends CommonDto {
    @Override
    public Integer getLength() {
        return 7;
    }

    @Override
    public String getCode() {
        return "c0";
    }

    @Override
    public void encode(ByteBuf out,StringBuilder outStr) {
        outStr.append("01");
        super.encode(out,outStr);
    }
}
