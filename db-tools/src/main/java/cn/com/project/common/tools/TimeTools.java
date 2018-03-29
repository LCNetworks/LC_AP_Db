/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hanchuanjun
 */
public class TimeTools {
    //"yyyyMMddHHmmssSSS"
    public String formatTime(Long ts,String format){
        Long now = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat(format);

        return  df.format(ts);
    }
    
    public String formatTime(String aplusTime,String srcFormat,String dstFormat){
        try{
        DateFormat df = new SimpleDateFormat(srcFormat);
        Date d = df.parse(aplusTime);
        
        DateFormat df1 = new SimpleDateFormat(dstFormat);
        return df1.format(d);
        }catch(Exception e){
            return aplusTime;
        }
    }
    
    public String getStdTime(String da,String he){
        try{
            DateFormat df = null;
            if(da.trim().length() == 8){
                df = new SimpleDateFormat("yyyyMMddHHmmss");
            } else if(da.trim().length() == 10){
                df = new SimpleDateFormat("yyyy-MM-ddHHmmss");
            }
            if(he.trim().length() == 4){
                he = he.trim() + "00";
            }
            Date d = df.parse(da.trim() + he);
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df1.format(d);
        }catch(Exception e){
            return null;
        }
    }

    //da=yyyy-MM-dd     he=HHmm   return = yyyy-MM-dd HH:mm
    public String getTimeSub(String da,String he){
        if(da != null && !da.trim().equals("") && he != null && !he.trim().equals("")){
            return da.trim()+" "+he.trim().substring(0,2)+":"+he.trim().substring(2);
        } else{
            return null;
        }
    }

    public String getHeTime(String ts){
        try{
            String timeFm = "yyyy-MM-dd HH:mm";
            if(ts.trim().length() > 16){
                timeFm = "yyyy-MM-dd HH:mm:ss";
            }
            DateFormat df = new SimpleDateFormat("HHmm");
            DateFormat df1 = new SimpleDateFormat(timeFm);
            Date d = df1.parse(ts);

            return df.format(d);
        }catch(Exception e){
            return null;
        }
        
    }
    
    public String getDaDate(String ts, String format){
        try{
            String timeFm = "yyyy-MM-dd HH:mm";
            if(format == null){
                format = "yyyy-MM-dd";
            }
            if(ts.trim().length() > 16){
                timeFm = "yyyy-MM-dd HH:mm:ss";
            }
            DateFormat df = new SimpleDateFormat(format);
            DateFormat df1 = new SimpleDateFormat(timeFm);
            Date d = df1.parse(ts);
            return df.format(d);
        }catch(Exception e){
            return null;
        }
        
    }
    
    public Long getTimeStamp(String time, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime
     *            时间区间,半闭合,如[10:00-20:00)
     * @param curTime
     *            需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            }
            else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
    }
}
