package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @Date: 2021/3/15 17:25
 * @Description: TODO → RS485串口协议配置
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SerialPortProtocolSettingDto extends CommonDto {
    /**
     * 帧长度
     */
    Integer frameLength;

    /**
     * 设备上电等待时间
     */
    Integer waitPowerOnTime;

    /**
     * 总线挂接设备个数
     */
    Integer deviceNum;

    /**
     * 变量个数
     */
    Integer variateNum;

    /**
     * 变量List
     */
    List<Map<String, Integer>> valueList;

    @Override
    public Integer getLength() {
        return this.getFrameLength();
    }

    @Override
    public String getCode() {
        return "28";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //设备上电等待时间
        outStr.append(String.format("%02X", this.getWaitPowerOnTime()))
                //总线挂接设备个数
                .append(String.format("%02X", this.getDeviceNum()))
                //变量个数
                .append(String.format("%02X", this.getVariateNum()));
        for (int a = 0; a < this.getVariateNum(); a++) {
            //地址号
            String address = String.format("%02X", this.getValueList().get(a).get("address"));
            //功能码
            String functionCode = String.format("%02X", this.getValueList().get(a).get("functionCode"));
            //起始寄存器号
            String startNo = String.format("%04X", this.getValueList().get(a).get("startNo"));
            //数据类型
            String dataType = String.format("%02X", this.getValueList().get(a).get("dataType"));
            //精度/浮点数字节组合方式
            String byteModel = String.format("%02X", this.getValueList().get(a).get("byteModel"));
            outStr.append(address).append(functionCode).append(startNo).append(dataType).append(byteModel);
        }
        super.encode(out, outStr);
    }
}
