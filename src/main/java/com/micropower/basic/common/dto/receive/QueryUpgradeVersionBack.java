package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Date: 2020/12/28 13:15
 * @Description: TODO →.查询当前文件传输包 返回
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUpgradeVersionBack extends CommonDto {
    /**
     * 总包数
     */
    Integer totalPackage;

    /**
     *  版本号
     */
    Integer versionNo;

    /**
     * 当前包数
     */
    Integer packageNo;

    @Override
    protected void decode(String in, CommonDto soketCommonDto) {
        QueryUpgradeVersionBack back=(QueryUpgradeVersionBack)soketCommonDto;
        back.setVersionNo(Integer.valueOf(in.substring(0,4),16));
        back.setTotalPackage(Integer.valueOf(in.substring(4,8),16));
        back.setPackageNo(Integer.valueOf(in.substring(8,12),16));
    }
}
