package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/12/28 11:37
 * @Description: TODO → 查询当前文件传输包
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUpgradeVersion extends CommonDto {
    /**
     * 帧长度
     */
    Integer frameLength;

    /**
     * 总包数
     */
    Integer totalPackage;

    /**
     * 版本号
     */
    Integer versionNo;

    @Override
    public Integer getLength() {
        return 13;
    }

    @Override
    public String getCode() {
        return "25";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //文件版本
        outStr.append(String.format("%04X", this.getVersionNo()))
                //文件总包数
                .append(String.format("%04X", this.getTotalPackage()));
        super.encode(out, outStr);
    }
}
