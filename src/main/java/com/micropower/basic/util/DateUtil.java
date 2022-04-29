package com.micropower.basic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
