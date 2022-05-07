package com.micropower.basic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kohaku_川
 * @description TODO 时间工具类
 * @date 2022/4/26 17:20
 */
public class DateUtil {

    private DateUtil() {
    }

    public static Date parseStr2Date(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(str);
    }

    public static String parseDate2Str(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获取前后n小时整点时间Stirng
     *
     * @param n 前后多少小时
     * @return 时间
     */
    public static Date beforeHourDateToNow(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -n);
        return calendar.getTime();
    }

    public static String beforeHourStrToNow(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, -n);
        return parseDate2Str(calendar.getTime());
    }

    public static Map<String, Object> getOneHourAgoRange() {
        return parseDate2StrRange(beforeHourDateToNow(1));
    }

    private static Map<String, Object> parseDate2StrRange(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        String beginTime = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":00:00";
        String endTime = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":59:59";
        String range = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d) + " " + h + ":00" + " ~ " + (h + 1) + ":00";
        Map<String, Object> result = new HashMap<>(3);
        result.put("beginTime", beginTime);
        result.put("endTime", endTime);
        result.put("range", range);
        return result;
    }

    /**
     * 获取前后n小时整点时间Stirng
     *
     * @param n 前后多少小时
     * @return 时间
     */
    public static Date beforeDayDateToNow(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -n);
        return calendar.getTime();
    }

    public static String beforeDayStrToNow(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -n);
        return parseDate2Str(calendar.getTime());
    }

    public static Map<String, Object> getOneDayAgoRange() {
        return parseDate2StrRange2(beforeDayDateToNow(1));
    }

    private static Map<String, Object> parseDate2StrRange2(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        String day = y + "-" + String.format("%02d", m) + "-" + String.format("%02d", d);
        String beginTime = day + " " + "00:00:00";
        String endTime = day + " " + "23:59:59";
        Map<String, Object> result = new HashMap<>(3);
        result.put("beginTime", beginTime);
        result.put("endTime", endTime);
        result.put("range", day);
        return result;
    }
}
