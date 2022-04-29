package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2021/3/15 16:15
 * @Description: TODO → 设置内置液位传感器类型
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class BuiltInLevelSensorSettingDto extends CommonDto {
    /**
     * 内置液位传感器通信类型
     */
    String type;

    @Override
    public Integer getLength() {
        return 10;
    }

    @Override
    public String getCode() {
        return "11";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        outStr.append(this.getType());
        super.encode(out, outStr);
    }
}
