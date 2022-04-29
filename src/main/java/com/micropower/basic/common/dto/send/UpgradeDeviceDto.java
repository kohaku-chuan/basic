package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Date: 2020/9/18 16:37
 * @Description: TODO →
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeDeviceDto extends CommonDto {
    /**
     * 帧长度
     */
    Integer frameLength;

    /**
     * 总包数
     */
    Integer totalPackage;

    /**
     * 当前包数
     */
    Integer packageNo;

    /**
     * 当前包数字节数
     */
    Integer packageBytes;

    /**
     * 内容
     */
    String content;

    /**
     *
     */
    List<String> packageContent;

    @Override
    public Integer getLength() {
        return this.getFrameLength();
    }

    @Override
    public String getCode() {
        return "1E";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        outStr//总包数
                .append(String.format("%04X", this.getTotalPackage()))
                //当前包序号
                .append(String.format("%04X", this.getPackageNo()))
                //当前包长度
                .append(String.format("%04X", this.getPackageBytes()))
                //.升级内容
                .append(this.getContent());
        super.encode(out, outStr);
    }

}
