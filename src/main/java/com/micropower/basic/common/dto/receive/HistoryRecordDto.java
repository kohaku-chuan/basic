package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Date: 2020/9/18 17:08
 * @Description: TODO → 查询历史纪录
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class HistoryRecordDto extends CommonDto {
    /**
     * 历史记录总条数
     */
    int totalRecordNumber;

    /**
     * 当前包日志条数
     */
    int recordNumber;

    /**
     * 结束时间
     */
    String lastTime;

    List<HistoryChildRecord> historyChildRecordList;

    @Override
    protected void decode(String in, CommonDto commonDto) {
        HistoryRecordDto historyRecordDto = (HistoryRecordDto) commonDto;
        int totalRecordNumber = Integer.parseInt(in.substring(0, 2), 16);
        historyRecordDto.setTotalRecordNumber(totalRecordNumber);
        if (totalRecordNumber > 0) {
            int recordNumber = Integer.parseInt(in.substring(2, 4), 16);
            historyRecordDto.setRecordNumber(recordNumber);
            List<HistoryChildRecord> list = new ArrayList<>();
            String recordStr = in.substring(4);
            int oneRecordLength = recordStr.length() / recordNumber;
            Integer x = 0;
            //模拟通道变量个数》0
            if (oneRecordLength > 54) {
                for (int i = 0; i < recordNumber; i++) {
                    List<String> valueList = new ArrayList<>();
                    Integer basicNum = i * (54 + 8 * x);
                    String level = DecoderUtil.get1032Value(recordStr.substring(basicNum, 8 + basicNum));
                    Integer num = Integer.valueOf(recordStr.substring(basicNum + 8, basicNum + 10), 16);
                    String valueStr = recordStr.substring(basicNum + 10, 10 + basicNum + 8 * num);
                    for (int a = 0; a < valueStr.length(); a++) {
                        String oneValue = DecoderUtil.get1032Value(valueStr.substring(a * 8, a * 8 + 8));
                        valueList.add(oneValue);
                    }
                    List<String> valueList2 = new ArrayList<>();
                    String channel1Value = DecoderUtil.get1032Value(recordStr.substring(10 + basicNum + 8 * num, 18 + basicNum + 8 * num));
                    valueList2.add(channel1Value);
                    String channel2Value = DecoderUtil.get1032Value(recordStr.substring(18 + basicNum + 8 * num, 26 + basicNum + 8 * num));
                    valueList2.add(channel2Value);
                    String channel3Value = DecoderUtil.get1032Value(recordStr.substring(26 + basicNum + 8 * num, 34 + basicNum + 8 * num));
                    valueList2.add(channel3Value);
                    String channel4Value = DecoderUtil.get1032Value(recordStr.substring(34 + basicNum + 8 * num, 42 + basicNum + 8 * num));
                    valueList2.add(channel4Value);
                    String time = DecoderUtil.parseDate(recordStr.substring(42 + basicNum + 8 * num, 54 + basicNum + 8 * num));

                    HistoryChildRecord historyChildRecord = new HistoryChildRecord();
                    historyChildRecord.setAreaCode(commonDto.getAreaCode());
                    historyChildRecord.setAddress(commonDto.getAddress());
                    historyChildRecord.setLevel(level);
                    historyChildRecord.setR485ChannelNum(num);
                    historyChildRecord.setR485ChannelValue(valueList);
                    historyChildRecord.setAnalogValue(valueList2);
                    historyChildRecord.setSampleTime(time);
                    list.add(historyChildRecord);
                    if (i + 1 == recordNumber) {
                        historyRecordDto.setLastTime(time);
                    }
                    x = num;
                }
                historyRecordDto.setHistoryChildRecordList(list);
            } else {
                for (int i = 0; i < recordNumber; i++) {
                    String level = DecoderUtil.get1032Value(recordStr.substring(i * 54, 8 + i * 54));
                    Integer num = Integer.valueOf(recordStr.substring(8 + i * 54, 10 + i * 54), 16);
                    List<String> valueList = new ArrayList<>();
                    String channel1Value = DecoderUtil.get1032Value(recordStr.substring(10 + i * 54, 18 + i * 54));
                    valueList.add(channel1Value);
                    String channel2Value = DecoderUtil.get1032Value(recordStr.substring(18 + i * 54, 26 + i * 54));
                    valueList.add(channel2Value);
                    String channel3Value = DecoderUtil.get1032Value(recordStr.substring(26 + i * 54, 34 + i * 54));
                    valueList.add(channel3Value);
                    String channel4Value = DecoderUtil.get1032Value(recordStr.substring(34 + i * 54, 42 + i * 54));
                    valueList.add(channel4Value);
                    String time = DecoderUtil.parseDate(recordStr.substring(42 + i * 54, 54 + i * 54));
                    HistoryChildRecord historyChildRecord = new HistoryChildRecord();
                    historyChildRecord.setAreaCode(commonDto.getAreaCode());
                    historyChildRecord.setAddress(commonDto.getAddress());
                    historyChildRecord.setLevel(level);
                    historyChildRecord.setR485ChannelNum(num);
                    historyChildRecord.setR485ChannelValue(null);
                    historyChildRecord.setAnalogValue(valueList);
                    historyChildRecord.setSampleTime(time);
                    list.add(historyChildRecord);
                    if (i + 1 == recordNumber) {
                        historyRecordDto.setLastTime(time);
                    }
                }
                historyRecordDto.setHistoryChildRecordList(list);
            }
        }
    }
}
