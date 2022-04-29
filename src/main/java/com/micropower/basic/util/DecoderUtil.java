package com.micropower.basic.util;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.shade.org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @Date: 2021/2/9 10:00
 * @Description: TODO → 报文解码/编码辅助工具类
 * @Author:Kohaku_川
 **/
@Slf4j
public class DecoderUtil {

    public static String receiveHexToString(byte[] by) {
        try {
            String str = bytes2Str(by);
            str = Objects.requireNonNull(str).toUpperCase();
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("接收字节数据并转为16进制字符串异常");
        }
        return null;
    }

    public static String bytes2Str(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String toHex(byte b) {
        try {
            String result = Integer.toHexString(b & 0xFF);
            if (result.length() == 1) {
                result = '0' + result;
            }
            return result;
        } catch (Exception e) {
            log.error("读取16进制字符串异常", e);
        }
        return null;
    }

    public static int byteToInt(byte b) {
        return b & 0xFF;
    }

    public static String byteToHex(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte datum : data) {
            sb.append(DIGITS[(datum >>> 4) & 0x0F]);
            sb.append(DIGITS[datum & 0x0F]);
        }
        return sb.toString();
    }

    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String get1032Value(String in) {
        String by1 = in.substring(0, 2);
        String by2 = in.substring(2, 4);
        String by3 = in.substring(4, 6);
        String by4 = in.substring(6);
        String byte1 = by2 + by1 + by4 + by3;
        byte[] a = DecoderUtil.toBytes(byte1);
        float m = DecoderUtil.byte2float(a, 0);
        String strValue = String.format("%.3f", m);
        return "4294967296.000".equals(strValue) ? "异常" : strValue;
    }

    public static void main(String[] args) {
        System.out.println(get1032Value("FFFFFFFF"));
    }

    public static String getTwoByte(String in) {
        String by1 = in.substring(0, 2);
        String by2 = in.substring(2, 4);
        String byte1 = by2 + by1;
        byte[] a = DecoderUtil.toBytes(byte1);
        float m = DecoderUtil.byte2short(a);
        return String.format("%.2f", m);
    }

    /**
     * 将16进制字符串转换为byte[]
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 这个函数将byte转换成float
     **/
    private static float byte2float(byte[] b, int index) {
        int l;
        l = b[index];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static int dwordBytesToLong(String msg) {
        String by1 = msg.substring(0, 2);
        String by2 = msg.substring(2, 4);
        String byte1 = by1 + by2;
        byte[] data = DecoderUtil.toBytes(byte1);
        return byte2short(data);
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8;
            l |= (b[i] & 0xff);
        }
        return l;
    }

    public static String parseDate(String msg) {
        String year = "20" + msg.substring(0, 2);
        String month = msg.substring(2, 4);
        String day = msg.substring(4, 6);
        String hour = msg.substring(6, 8);
        String min = msg.substring(8, 10);
        String second = msg.substring(10);
        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + second;
    }

    public static String parseDate2(String msg) {
        String year = "20" + msg.substring(0, 2);
        String month = msg.substring(2, 4);
        String day = msg.substring(4, 6);
        String hour = msg.substring(6, 8);
        return year + "-" + month + "-" + day + " " + hour;
    }

    public static byte[] double2bytes1032(double a) {
        String hex = Integer.toHexString(Float.floatToIntBits((float) a));
        return hexStr2bytes(hex.substring(4, 6) + hex.substring(6) + hex.substring(0, 2) + hex.substring(2, 4));
    }

    public static String doubleTo1032Hex(double a) {
        String hex = String.format("%08x", Float.floatToIntBits((float) a));
        return hex.substring(4, 6) + hex.substring(6) + hex.substring(0, 2) + hex.substring(2, 4);
    }

    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        return a;
    }

    public static StringBuilder getDate(Date date, StringBuilder out, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        year = year % 100;
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        out.append(String.format("%02d", year));
        out.append(String.format("%02d", month));
        out.append(String.format("%02d", day));
        out.append(String.format("%02d", hour));
        out.append(String.format("%02d", minute));
        if (num > 5) {
            out.append(String.format("%02d", second));
        }
        return out;
    }

    /**
     * 求十六进制累加和-1字节校验
     **/
    public static String makeCheckSum(String data) {
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        //用256求余最大是255，即16进制的FF
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static byte[] hexStr2bytes(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return null;
        }
        if (hexStr.length() % 2 != 0) {
            hexStr = "0" + hexStr;
        }
        char[] chars = hexStr.toCharArray();
        int len = chars.length / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = i * 2;
            bytes[i] = (byte) Integer.parseInt(String.valueOf(new char[]{chars[x], chars[x + 1]}), 16);
        }
        return bytes;
    }

    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static String getTimeByMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    //这个函数将float转换成byte[]
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    public static String calcCrc16(String text) {
        byte[] data = text.getBytes();
        int crc = 0xffff;
        int dxs = 0xa001;
        int hibyte;
        int sbit;
        for (byte datum : data) {
            hibyte = crc >> 8;
            crc = hibyte ^ datum;

            for (int j = 0; j < 8; j++) {
                sbit = crc & 0x0001;
                crc = crc >> 1;
                if (sbit == 1) {
                    crc ^= dxs;
                }
            }
        }
        return Integer.toHexString(crc & 0xffff);
    }

    public static String lengthStr(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        if (length < 10) {
            stringBuilder.append("000").append(length);
        } else if (length < 100) {
            stringBuilder.append("00").append(length);
        } else {
            stringBuilder.append("0").append(length);
        }
        return stringBuilder.toString();
    }

    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    /**
     * 计算CRC16/Modbus校验码  低位在前,高位在后
     *
     * @param str 十六进制字符串
     * @return
     */
    public static String getModbusCRC(String str) {
        byte[] bytes = toBytes(str);
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        String crc = Integer.toHexString(CRC);
        if (crc.length() == 2) {
            crc = "00" + crc;
        } else if (crc.length() == 3) {
            crc = "0" + crc;
        }
        crc = crc.substring(2, 4) + crc.substring(0, 2);
        return crc.toUpperCase();
    }
}
