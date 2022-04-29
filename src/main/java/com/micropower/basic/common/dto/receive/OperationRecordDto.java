package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2020/9/18 17:07
 * @Description: TODO →查询后返回的操作记录
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class OperationRecordDto extends CommonDto {
    /**
     * 操作日志总条数
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

    /**
     * 操作日志
     */
    List<OperationChildRecord> operationChildRecordList;

    @Override
    protected void decode(String in, CommonDto soketCommonDto) {
        OperationRecordDto operationRecordDto = (OperationRecordDto) soketCommonDto;
        int totalRecordNumber = Integer.parseInt(in.substring(0,2),16);
        operationRecordDto.setTotalRecordNumber(totalRecordNumber);
        if(totalRecordNumber!=0) {
            int recordNumber = Integer.parseInt(in.substring(2, 4), 16);
            operationRecordDto.setRecordNumber(recordNumber);
            List<OperationChildRecord> list = new ArrayList<>();
            String recordStr = in.substring(4, 4 + recordNumber * 32);
            for (int a = 1; a <= recordNumber; a++) {
                String oneRecord=recordStr.substring((a - 1) * 32, (a - 1) * 32 + 32);
                String time = DecoderUtil.parseDate(oneRecord.substring(0,12));
                if (a == recordNumber) {
                    operationRecordDto.setEndTime(time);
                }
                String source = "";
                switch (Integer.valueOf(oneRecord.substring(12,14))) {
                    case 0:
                        source = "MCU自主操作";
                        break;
                    case 1:
                        source = "平台操作";
                        break;
                    case 2:
                        source = "本地蓝牙操";
                        break;
                    default:
                        break;
                }
                String type = Integer.valueOf(oneRecord.substring(14,16)) == 0 ? "初始化（MCU自主操作）" : "其他命令操作";
                String cmdType=oneRecord.substring(16,18);
                OperationChildRecord operationChildRecord = new OperationChildRecord();
                operationChildRecord.setAddress(operationRecordDto.getAddress());
                operationChildRecord.setAreaCode(operationRecordDto.getAreaCode());
                operationChildRecord.setTime(time);
                operationChildRecord.setSource(source);
                operationChildRecord.setType(type);
                operationChildRecord.setCmdType(cmdType);
                list.add(operationChildRecord);
            }
            operationRecordDto.setOperationChildRecordList(list);
        }
    }
}
