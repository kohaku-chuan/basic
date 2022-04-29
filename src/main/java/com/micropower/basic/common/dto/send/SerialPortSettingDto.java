package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:33
 * @Description: TODO →设置RS485串口参数
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SerialPortSettingDto extends CommonDto {
    /**
     * 波特率
     */
    String baudRate;

    /**
     * 数据位
     */
    String dataDigit;

    /**
     * 校验位
     */
    String checkDigit;

    /**
     * 停止位
     */
    String stopDigit;

    @Override
    public Integer getLength() {
        return 13;
    }

    @Override
    public String getCode() {
        return "c6";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //波特率
        outStr.append(this.getBaudRate())
                //数据位
                .append(this.getDataDigit())
                //校验位
                .append(this.getCheckDigit())
                //停止位
                .append(this.getStopDigit());
        super.encode(out, outStr);
    }
}
