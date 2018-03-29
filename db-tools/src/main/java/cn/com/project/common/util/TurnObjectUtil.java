package cn.com.project.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TurnObjectUtil {

    public static String turnStringToString(String str) {
        StringBuffer idsBuf = new StringBuffer();
        if (str != null && str.indexOf(",") != -1) {
            String ids[] = str.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (i == ids.length - 1) {
                    idsBuf.append("'").append(ids[i]).append("'");
                } else {
                    idsBuf.append("'").append(ids[i]).append("'").append(",");
                }
            }
        } else if (str != null && str.trim().length() == 0) {
            idsBuf.append("'").append("-1").append("'");
        } else {
            idsBuf.append("'").append(str).append("'");
        }
        return idsBuf.toString();
    }

    //把日期转为字符串 
    public static String ConverToString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    //把字符串转为日期 
    public static Date ConverToDate(String strDate, String format) throws Exception {
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(strDate);
    }

    /**
     * 把时间转换成秒数 作为偏移量
     *
     * @param datestr
     * @return Long 时间对应的秒数
     */
    public static Long paserTimeOffset(String datestr, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = new Date();
        try {
            date = format.parse(datestr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Long times = date.getTime() / 1000;
        return times;
    }

    /**
     * 把秒数转换成北京时间
     *
     * @param timestampString 秒数的字符串形式
     * @param formats         转换的时间格式
     * @return 转换后的时间
     */
    public static String TimeStamp2Date(Long timestampString, String formats) {
        Long timestamp = timestampString * 1000L;
        String date = new java.text.SimpleDateFormat(formats)
                .format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 解决中文乱码
     *
     * @param s
     * @return
     */
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception ex) {
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0) k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param amount
     * @return
     */
    public static String formatAmount(String amount) {
        DecimalFormat format = new DecimalFormat();
        format.applyPattern("##,###");
        return format.format(Long.parseLong(amount));
    }

    /**
     * @param amount
     * @param pattern
     * @return
     */
    public static String formatPattern(String amount, String pattern) {
        DecimalFormat format = new DecimalFormat();
        format.applyPattern(pattern);
        return format.format(Double.parseDouble(amount));
    }

    public static void main(String args[]) {
        Long s = paserTimeOffset("20140101", "yyyyMMdd");
        String d = TimeStamp2Date(s, "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        int dd = calendar.get(Calendar.YEAR);
        String invoiceDate1 = TimeStamp2Date(calendar.getTimeInMillis() / 1000, "yyyyMMdd");
    }

    /**
     * 根据一段时间区间，按月份拆分成多个时间段
     * @param startDate 开始日期
     * @param endDate  结束日期
     * @return
     */
    public static List<String> getMonthSort(String startDate, String endDate) {
        List<String> list = new ArrayList<String>();
        try {
            Date d1 = new SimpleDateFormat("yyyy-MM").parse(startDate);// 定义起始日期
            Date d2 = new SimpleDateFormat("yyyy-MM").parse(endDate);// 定义结束日期
            // 定义日期实例
            Calendar dd = Calendar.getInstance();
            dd.setTime(d1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            while (dd.getTime().before(d2)) {     // 判断是否到结束日期
                list.add(sdf.format(dd.getTime()));
                dd.add(Calendar.MONTH, 1);  //进行当前日期月份加1
            }
            list.add(sdf.format(dd.getTime()));
        } catch (ParseException e) {
            return null;
        }
        return list;
    }

}