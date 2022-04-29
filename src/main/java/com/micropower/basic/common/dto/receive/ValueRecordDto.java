package com.micropower.basic.common.dto.receive;

import com.micropower.basic.common.dto.CommonDto;
import com.micropower.basic.util.DecoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * @Date: 2020/9/18 17:07
 * @Description: TODO →周期上报数据
 * @Author:Kohaku_川
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ValueRecordDto extends CommonDto {

    /**
     * 上报总包数1~6
     */
    int totalPackages;

    /**
     * 数据List
     */
    List<HistoryChildRecord> valueList;

    @Override
    protected void decode(String in, CommonDto commonDto) {
        ValueRecordDto dto = (ValueRecordDto) commonDto;
        int totalPackages = Integer.valueOf(in.substring(0, 2), 16);
        dto.setTotalPackages(totalPackages);
        List<HistoryChildRecord> list = new ArrayList<>();
        String recordStr = in.substring(2);
        Integer basicNum = 0;
        for (int i = 0; i < totalPackages; i++) {
            List<String> valueList = new ArrayList<>();
            String level = DecoderUtil.get1032Value(recordStr.substring(basicNum, 8 + basicNum));
            Integer num = Integer.valueOf(recordStr.substring(basicNum + 8, basicNum + 10), 16);
            if (num != 0) {
                String valueStr = recordStr.substring(basicNum + 10, 10 + basicNum + 8 * num);
                for (int a = 0; a < valueStr.length() / 8; a++) {
                    String oneValue = DecoderUtil.get1032Value(valueStr.substring(a * 8, a * 8 + 8));
                    valueList.add(oneValue);
                }
            }
            List<String> valueList2 = new ArrayList<>();
            String channel1Value = DecoderUtil.get1032Value(recordStr.substring(10 + basicNum + 8 * num, 18 + basicNum + 8 * num));
            String channel2Value = DecoderUtil.get1032Value(recordStr.substring(18 + basicNum + 8 * num, 26 + basicNum + 8 * num));
            String channel3Value = DecoderUtil.get1032Value(recordStr.substring(26 + basicNum + 8 * num, 34 + basicNum + 8 * num));
            String channel4Value = DecoderUtil.get1032Value(recordStr.substring(34 + basicNum + 8 * num, 42 + basicNum + 8 * num));
            valueList2.add(channel1Value);
            valueList2.add(channel2Value);
            valueList2.add(channel3Value);
            valueList2.add(channel4Value);
            String time = DecoderUtil.parseDate(recordStr.substring(42 + basicNum + 8 * num, 54 + basicNum + 8 * num));

            HistoryChildRecord historyChildRecord = new HistoryChildRecord();
            historyChildRecord.setAreaCode(dto.getAreaCode());
            historyChildRecord.setAddress(dto.getAddress());
            historyChildRecord.setLevel(level);
            historyChildRecord.setR485ChannelNum(num);
            historyChildRecord.setR485ChannelValue(valueList);
            historyChildRecord.setAnalogValue(valueList2);
            historyChildRecord.setSampleTime(time);
            list.add(historyChildRecord);
            basicNum += num * 8 + 54;
        }
        dto.setValueList(list);
    }

    public static ValueRecordDto parseDto(String in) {
        ValueRecordDto dto = new ValueRecordDto();
        int totalPackages = Integer.valueOf(in.substring(0, 2), 16);
        dto.setTotalPackages(totalPackages);
        List<HistoryChildRecord> list = new ArrayList<>();
        String recordStr = in.substring(2);
        Integer basicNum = 0;
        for (int i = 0; i < totalPackages; i++) {
            List<String> valueList = new ArrayList<>();
            String level = DecoderUtil.get1032Value(recordStr.substring(basicNum, 8 + basicNum));
            Integer num = Integer.valueOf(recordStr.substring(basicNum + 8, basicNum + 10), 16);
            if (num != 0) {
                String valueStr = recordStr.substring(basicNum + 10, 10 + basicNum + 8 * num);
                for (int a = 0; a < valueStr.length() / 8; a++) {
                    String oneValue = DecoderUtil.get1032Value(valueStr.substring(a * 8, a * 8 + 8));
                    valueList.add(oneValue);
                }
            }
            List<String> valueList2 = new ArrayList<>();
            String channel1Value = DecoderUtil.get1032Value(recordStr.substring(10 + basicNum + 8 * num, 18 + basicNum + 8 * num));
            String channel2Value = DecoderUtil.get1032Value(recordStr.substring(18 + basicNum + 8 * num, 26 + basicNum + 8 * num));
            String channel3Value = DecoderUtil.get1032Value(recordStr.substring(26 + basicNum + 8 * num, 34 + basicNum + 8 * num));
            String channel4Value = DecoderUtil.get1032Value(recordStr.substring(34 + basicNum + 8 * num, 42 + basicNum + 8 * num));
            valueList2.add(channel1Value);
            valueList2.add(channel2Value);
            valueList2.add(channel3Value);
            valueList2.add(channel4Value);
            String time = DecoderUtil.parseDate(recordStr.substring(42 + basicNum + 8 * num, 54 + basicNum + 8 * num));

            HistoryChildRecord historyChildRecord = new HistoryChildRecord();
            historyChildRecord.setAreaCode(dto.getAreaCode());
            historyChildRecord.setAddress(dto.getAddress());
            historyChildRecord.setLevel(level);
            historyChildRecord.setR485ChannelNum(num);
            historyChildRecord.setR485ChannelValue(valueList);
            historyChildRecord.setAnalogValue(valueList2);
            historyChildRecord.setSampleTime(time);
            list.add(historyChildRecord);
            basicNum += num * 8 + 54;
        }
        dto.setValueList(list);
        return dto;
    }
}
