package com.micropower.basic.common.dto.send;


import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Date: 2020/9/18 16:34
 * @Description: TODO → 查询操作日志
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryOperationRecordDto extends CommonDto {
    /**
     * 开始查询时间
     */
    Date beginTime;

    /**
     * 结束查询时间
     */
    Date endTime;

    @Override
    public Integer getLength() {
        return 19;
    }

    @Override
    public String getCode() {
        return "c9";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        DecoderUtil.getDate(this.getBeginTime(), outStr, 5);
        DecoderUtil.getDate(this.getEndTime(), outStr, 5);
        super.encode(out, outStr);
    }
}
