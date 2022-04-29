package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ValueRecordBackDto extends CommonDto {
    /**
     * 是否成功
     */
    Integer isSuccess;

    /**
     * 对应包序号
     */
    Integer packageSerialNumber;

    @Override
    public Integer getLength() {
        return 8;
    }

    @Override
    public String getCode() {
        return "d1";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        outStr.append(String.format("%02X", this.getIsSuccess())).append(String.format("%02X", this.getPackageSerialNumber()));
        super.encode(out, outStr);
    }
}
