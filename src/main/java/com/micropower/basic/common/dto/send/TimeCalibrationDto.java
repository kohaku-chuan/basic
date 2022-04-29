package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;

import java.util.Date;

/**
 * @Date: 2020/7/9 11:33
 * @Description: TODO →校准系统时间
 * @Author:Kohaku_川
 **/
public class TimeCalibrationDto extends CommonDto {

    @Override
    public Integer getLength() {
        return 15;
    }

    @Override
    public String getCode() {
        return "c8";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        DecoderUtil.getDate(new Date(), outStr, 6);
        super.encode(out, outStr);
    }
}
