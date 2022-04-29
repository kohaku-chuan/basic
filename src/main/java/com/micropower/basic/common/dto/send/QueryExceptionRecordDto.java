package com.micropower.basic.common.dto.send;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Date: 2020/9/18 16:35
 * @Description: TODO → 查询异常记录
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryExceptionRecordDto extends CommonDto {
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
        return "ca";
    }

    @Override
    public void encode(ByteBuf out, StringBuilder outStr) {
        DecoderUtil.getDate(this.getBeginTime(), outStr, 5);
        DecoderUtil.getDate(this.getEndTime(), outStr, 5);
        super.encode(out, outStr);
    }
}
