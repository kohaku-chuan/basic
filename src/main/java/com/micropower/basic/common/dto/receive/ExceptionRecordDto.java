package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2020/9/18 17:07
 * @Description: TODO → 查询异常的返回记录
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ExceptionRecordDto extends CommonDto {
    /**
     * 异常日志总条数
     */
    int totalRecordNumber;

    /**
     * 当前包日志条数
     */
    int recordNumber;

    /**
     * 结束时间
     */
    String endTime;

    List<ExceptionChildRecord> exceptionChildRecords;

    @Override
    protected void decode(String in, CommonDto soketCommonDto) {
        ExceptionRecordDto exceptionRecordDto = (ExceptionRecordDto) soketCommonDto;
        int totalRecordNumber = Integer.parseInt(in.substring(0, 2), 16);
        exceptionRecordDto.setTotalRecordNumber(totalRecordNumber);
        if (totalRecordNumber > 0) {
            int recordNumber = Integer.parseInt(in.substring(2, 4), 16);
            exceptionRecordDto.setRecordNumber(recordNumber);
            List<ExceptionChildRecord> list = new ArrayList<>();
            String recordStr = in.substring(4, 4 + recordNumber * 32);
            for (int a = 1; a <= recordNumber; a++) {
                String oneRecord = recordStr.substring((a - 1) * 32, (a - 1) * 32 + 32);
                String time = DecoderUtil.parseDate(oneRecord.substring(0, 12));
                if (a == recordNumber) {
                    exceptionRecordDto.setEndTime(time);
                }
                String type = "";
                switch (Integer.valueOf(oneRecord.substring(12, 14))) {
                    case 1:
                        type = "复位";
                        break;
                    case 2:
                        type = "基本参数初始化";
                        break;
                    case 3:
                        type = "上行参数初始化";
                        break;
                    case 4:
                        type = "采集参数初始化";
                        break;
                    case 5:
                        type = "校准参数初始化";
                        break;
                    case 6:
                        type = "GPRS应答超时";
                        break;
                    case 7:
                        type = "GPRS串口超时";
                        break;
                    case 8:
                        type = "GPRS帧错误";
                        break;
                    case 9:
                        type = "液位传感器读取错误";
                        break;
                    case 10:
                        type = "外置485传感器读取错误";
                        break;
                    default:
                        break;
                }
                ExceptionChildRecord exceptionChildRecord = new ExceptionChildRecord();
                exceptionChildRecord.setDeviceNo(soketCommonDto.getAddress());
                exceptionChildRecord.setAreaNo(soketCommonDto.getAreaCode());
                exceptionChildRecord.setTime(time);
                exceptionChildRecord.setType(type);
                list.add(exceptionChildRecord);
            }
            exceptionRecordDto.setExceptionChildRecords(list);
        }
    }
}
