package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Date: 2020/12/28 11:32
 * @Description: TODO →断点续传
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UpgradeDeviceDto2 extends CommonDto {
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
     * 软件版本号
     */
    String softwareVersionNo;

    /**
     * 硬件版本号
     */
    String hardwareVersionNo;

    /**
     * 内容
     */
    String content;

    /**
     *
     */
    List<String> packageContent;

    /**
     * 进行升级的次数
     */
    Integer times;

    @Override
    public Integer getLength() {
        return this.getFrameLength();
    }

    @Override
    public String getCode() {
        return "24";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //文件版本
        outStr.append(String.format("%04X", Integer.valueOf(this.getSoftwareVersionNo())))
                //总包数
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
