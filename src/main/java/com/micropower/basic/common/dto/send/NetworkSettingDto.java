package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/7/9 11:32
 * @Description: TODO →设置网络参数
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class NetworkSettingDto extends CommonDto {
    /**
     * 数据中心1IP
     */
    String dataCenter1Ip;

    /**
     * 数据中心1端口
     */
    String dataCenter1Port;

    /**
     * 数据中心1通讯方式
     */
    String dataCenter1Model;

    /**
     * 数据中心2IP
     */
    String dataCenter2Ip;

    /**
     * 数据中心2端口
     */
    String dataCenter2Port;

    /**
     * 数据中心2通讯方式
     */
    String dataCenter2Model;

    /**
     * 数据中心3IP
     */
    String dataCenter3Ip;

    /**
     * 数据中心3端口
     */
    String dataCenter3Port;

    /**
     * 数据中心3通讯方式
     */
    String dataCenter3Model;

    @Override
    public Integer getLength() {
        return 30;
    }

    @Override
    public String getCode() {
        return "c5";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        //数据中心 1 IP+端口+通讯方式
        if (this.getDataCenter1Ip().isEmpty()) {
            outStr.append("FFFFFFFF");
        } else {
            String[] iP1 = this.getDataCenter1Ip().split("\\.");
            outStr.append(String.format("%02X",Integer.valueOf(iP1[0]))).append(String.format("%02X",Integer.valueOf(iP1[1])))
                    .append(String.format("%02X",Integer.valueOf(iP1[2]))).append(String.format("%02X",Integer.valueOf(iP1[3])));
        }
        outStr.append(this.getDataCenter1Port().isEmpty()?"FFFF":String.format("%04X", Integer.valueOf(this.getDataCenter1Port()))).append(this.getDataCenter1Model().isEmpty()?"FF":this.getDataCenter1Model());

        //数据中心 2 IP+端口+通讯方式
        if (this.getDataCenter2Ip().isEmpty()) {
            outStr.append("FFFFFFFF");
        } else {
            String[] iP2 = this.getDataCenter2Ip().split("\\.");
            outStr.append(String.format("%02X",Integer.valueOf(iP2[0]))).append(String.format("%02X",Integer.valueOf(iP2[1])))
                    .append(String.format("%02X",Integer.valueOf(iP2[2]))).append(String.format("%02X",Integer.valueOf(iP2[3])));
        }
        outStr.append(this.getDataCenter2Port().isEmpty()?"FFFF":String.format("%04X", Integer.valueOf(this.getDataCenter2Port()))).append(this.getDataCenter2Model().isEmpty()?"FF":this.getDataCenter2Model());

        //数据中心 3 IP+端口+通讯方式
        if (this.getDataCenter3Ip().isEmpty()) {
            outStr.append("FFFFFFFF");
        } else {
            String[] iP3 = this.getDataCenter3Ip().split("\\.");
            outStr.append(String.format("%02X",Integer.valueOf(iP3[0]))).append(String.format("%02X",Integer.valueOf(iP3[1])))
                    .append(String.format("%02X",Integer.valueOf(iP3[2]))).append(String.format("%02X",Integer.valueOf(iP3[3])));
        }
        outStr.append(this.getDataCenter3Port().isEmpty()?"FFFF":String.format("%04X",Integer.valueOf(this.getDataCenter3Port()))).append(this.getDataCenter3Model().isEmpty()?"FF":this.getDataCenter3Model());
        super.encode(out, outStr);
    }

}
