package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2021/3/15 16:06
 * @Description: TODO →设置终端逻辑地址
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AreaAddressSettingDto extends CommonDto {
    /**
     * 地区码
     */
    String areaNo;

    /**
     * 设备地址
     */
    Integer deviceNo;

    @Override
    public Integer getLength() {
        return 13;
    }

    @Override
    public String getCode() {
        return "10";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //地区码
        outStr.append(String.format("%04X", Integer.valueOf(this.getAreaNo())))
                //设备地址
                .append(String.format("%04X", this.getDeviceNo()));
        super.encode(out, outStr);
    }
}
