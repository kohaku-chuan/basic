package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:30
 * @Description: TODO →工作模式及相关参数设置
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelSettingDto extends CommonDto {
    /**
     * 运行模式
     */
    String model;

    /**
     * RTC自动更新开关
     */
    String autoRtc;

    /**
     * 液位预警值
     */
    String levelWarningValue;

    /**
     * 数据采集周期
     */
    String sampleCycle;

    /**
     * 正常上报时间间隔
     */
    String normalTimeInterval;

    /**
     * 预警上报时间间隔
     */
    String warningTimeInterval;

    @Override
    public Integer getLength() {
        return 18;
    }

    @Override
    public String getCode() {
        return "c2";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //运行模式
        outStr.append("FF")
                //RTC自动更新开关
                .append(this.getAutoRtc())
                //液位预警值
                .append(this.getLevelWarningValue().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getLevelWarningValue())))
                //数据采集周期
                .append(this.getSampleCycle().isEmpty() ? "FF" : String.format("%02X",Integer.valueOf(this.getSampleCycle())))
                //正常上报时间间隔
                .append(this.getNormalTimeInterval().isEmpty() ? "FF" : String.format("%02X",Integer.valueOf(this.getNormalTimeInterval())))
                //预警上报时间间隔
                .append(this.getWarningTimeInterval().isEmpty() ? "FF" : String.format("%02X",Integer.valueOf(this.getWarningTimeInterval())));
        super.encode(out, outStr);
    }
}
