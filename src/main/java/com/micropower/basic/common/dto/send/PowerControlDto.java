package com.micropower.basic.common.dto.send;


import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 16:31
 * @Description: TODO →设置扩展通道开/关 （功耗控制）
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class PowerControlDto extends CommonDto {
    /**
     * RS485串口功耗控制
     */
    String serialPortPowerControl;

    /**
     * 4-20mA通道1功耗控制
     */
    String channel1PowerControl;

    /**
     * 4-20mA通道2功耗控制
     */
    String channel2PowerControl;

    /**
     * 4-20mA通道3功耗控制
     */
    String channel3PowerControl;

    /**
     * 4-20mA通道4功耗控制
     */
    String channel4PowerControl;

    /**
     * 内置超声波液位计功耗控制
     */
    String ultrasonicLevelControl;

    @Override
    public Integer getLength() {
        return 15;
    }

    @Override
    public String getCode() {
        return "c7";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //RS485串口功耗控制
        outStr.append(this.getSerialPortPowerControl())
                //通道1
                .append(this.getChannel1PowerControl())
                //通道2
                .append(this.getChannel2PowerControl())
                //通道3
                .append(this.getChannel3PowerControl())
                //通道4
                .append(this.getChannel4PowerControl())
                //超声波液位计
                .append(this.getUltrasonicLevelControl());
        super.encode(out, outStr);
    }
}
