package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:30
 * @Description: TODO →液位测量辅助参数设置
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LevelSettingDto extends CommonDto {
    /**
     * 窨井深度
     */
    String depth;

    /**
     * 液位校准补偿
     */
    String levelCompensate;

    @Override
    public Integer getLength() {
        return 17;
    }

    @Override
    public String getCode() {
        return "c3";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //窨井深度
        outStr.append(this.getDepth().isEmpty()? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getDepth())))
                //液位校准补偿
                .append(this.getLevelCompensate().isEmpty() ? "FFFFFFFF" : DecoderUtil.doubleTo1032Hex(Double.valueOf(this.getLevelCompensate())));
        super.encode(out, outStr);
    }
}
