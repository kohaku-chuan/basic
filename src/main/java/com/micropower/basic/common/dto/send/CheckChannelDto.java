package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2021/3/15 17:49
 * @Description: TODO →4-20模拟通道校零设置
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckChannelDto extends CommonDto {
    /**
     * ADC通道号
     */
    String channelNo;

    @Override
    public Integer getLength() {
        return 10;
    }

    @Override
    public String getCode() {
        return "16";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //ADC通道号
        outStr.append(this.getChannelNo()).append("0000");
        super.encode(out, outStr);
    }
}
