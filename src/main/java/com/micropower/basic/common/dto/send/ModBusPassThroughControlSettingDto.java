package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2021/3/15 17:45
 * @Description: TODO →MODBUS透传控制
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ModBusPassThroughControlSettingDto extends CommonDto {
    /**
     * 电源控制
     */
    String powerControl;

    @Override
    public Integer getLength() {
        return 10;
    }

    @Override
    public String getCode() {
        return "29";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //电源控制
        outStr.append(this.getPowerControl());
        super.encode(out, outStr);
    }
}
