package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:31
 * @Description: TODO →设置水质因子参数
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class WaterFactorSettingDto extends CommonDto {
    /**
     * 4-20mA 因子1类型
     */
    String facter1Type;

    /**
     * 4-20mA 因子1量程
     */
    String facter1Range;

    /**
     * 4-20mA 因子2类型
     */
    String facter2Type;

    /**
     * 4-20mA 因子2量程
     */
    String facter2Range;

    /**
     * 4-20mA 因子3类型
     */
    String facter3Type;

    /**
     * 4-20mA 因子3量程
     */
    String facter3Range;

    /**
     * 4-20mA 因子4类型
     */
    String facter4Type;

    /**
     * 4-20mA 因子4量程
     */
    String facter4Range;

    @Override
    public Integer getLength() {
        return 29;
    }

    @Override
    public String getCode() {
        return "c4";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //因子1类型+量程
        outStr.append(this.getFacter1Type().isEmpty() ? "FF" : String.format("%02X", Integer.valueOf(this.getFacter1Type()))).append(this.getFacter1Range().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getFacter1Range())))
                //因子2类型+量程
                .append(this.getFacter2Type().isEmpty() ? "FF" : String.format("%02X", Integer.valueOf(this.getFacter2Type()))).append(this.getFacter2Range().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getFacter2Range())))
                //因子3类型+量程
                .append(this.getFacter3Type().isEmpty() ? "FF" : String.format("%02X", Integer.valueOf(this.getFacter3Type()))).append(this.getFacter3Range().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getFacter3Range())))
                //因子4类型+量程
                .append(this.getFacter4Type().isEmpty() ? "FF" : String.format("%02X", Integer.valueOf(this.getFacter4Type()))).append(this.getFacter4Range().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getFacter4Range())));
        super.encode(out, outStr);
    }

}
