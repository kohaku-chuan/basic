package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2021/3/15 17:49
 * @Description: TODO →4-20模拟通道起电延时设置
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPowerOnDelaySettingDto extends CommonDto {
    /**
     * ADC通道1延时时间
     */
    Integer channel1DelayTime;

    /**
     * ADC通道2延时时间
     */
    Integer channel2DelayTime;

    /**
     * ADC通道3延时时间
     */
    Integer channel3DelayTime;

    /**
     * ADC通道4延时时间
     */
    Integer channel4DelayTime;

    @Override
    public Integer getLength() {
        return 13;
    }

    @Override
    public String getCode() {
        return "2B";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //ADC通道1延时时间
        outStr.append(this.getChannel1DelayTime() == null ? "FF" : String.format("%02X", this.getChannel1DelayTime()))
                //ADC通道2延时时间
                .append(this.getChannel2DelayTime() == null ? "FF" : String.format("%02X", this.getChannel2DelayTime()))
                //ADC通道3延时时间
                .append(this.getChannel3DelayTime() == null ? "FF" : String.format("%02X", this.getChannel3DelayTime()))
                //ADC通道4延时时间
                .append(this.getChannel4DelayTime() == null ? "FF" : String.format("%02X", this.getChannel4DelayTime()));
        super.encode(out, outStr);
    }
}
