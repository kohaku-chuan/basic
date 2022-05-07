package com.micropower.basic.util;

import com.alibaba.fastjson.JSON;
import com.micropower.basic.entity.StationBean;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Kohaku_川
 * @description 综合工具类
 * @date 2021/4/16 16:02
 */
public class CommonUtil {

    private CommonUtil() {
    }

    public static int dateDiff(String startTime, String endTime) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long diff = 0;
        try {
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Math.toIntExact(diff % nd % nh / nm);
    }

    public static boolean checkDate(String str) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sd.setLenient(false);
            sd.parse(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String parseRSSI(String string) {
        double rssi = Double.valueOf(string);
        if (rssi <= 5) {
            return "极差";
        } else if (rssi > 5 && rssi <= 12) {
            return "较差";
        } else if (rssi > 12 && rssi <= 18) {
            return "一般";
        } else if (rssi > 18 && rssi <= 25) {
            return "良好";
        } else {
            return "优秀";
        }
    }

    public static String parseBack1032(String str) {
        return str.substring(2, 4) + str.substring(0, 2) + str.substring(6) + str.substring(4, 6);
    }

    public static List<Map<String, Object>> toListMap(String json) {
        List<Object> list = JSON.parseArray(json);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object object : list) {
            Map<String, Object> ret = (Map<String, Object>) object;
            mapList.add(ret);
        }
        return mapList;
    }

    public static boolean isDouble(String totalFlow) {
        try {
            Double.valueOf(totalFlow);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param
     * @description 圆型截面，水流面积计算
     * @author Kohaku_川
     * @date 2021/10/27 10:25
     */
    public static double getCircleSection(double diameter, double height) {
        return Math.pow(diameter / 2, 2) * ((((height - (diameter / 2)) / Math.pow(diameter / 2, 2)) * Math.sqrt((diameter - height) * height)) + Math.asin((height - (diameter / 2)) / (diameter / 2)) + (3.14 / 2));
    }

    public static String getRandomNumber(double min, double max) {
        return String.format("%.3f", (Math.random() * (max - min) + min));
    }

    /**
     * 获取字符串拼音的第一个字母
     *
     * @param chinese
     * @return
     */
    public static String toFirstChar(String chinese) {
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

    /**
     * 汉字转为拼音
     *
     * @param chinese
     * @return
     */
    public static String toPinyin(String chinese) {
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }

}
