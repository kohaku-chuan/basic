package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;

import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:29
 * @Description: TODO →切换设备工作状态
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SwitchoverWorkStateDto extends CommonDto {
    /**
     * 工作状态
     */
    String workState;

    @Override
    public Integer getLength() {
        return 10;
    }

    @Override
    public String getCode() {
        return "c1";
    }

    @Override
    public void encode(ByteBuf out,StringBuilder outStr) {
        //工作状态
        outStr.append(this.getWorkState());
        super.encode(out,outStr);
    }
}
