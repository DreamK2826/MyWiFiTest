package com.dreamk.mywifitest.util;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public abstract class TimeUtil {
    public static String cuTime = " ";
    public static String cuWeekDay = " ";

    /**
     * 获取当前系统时间
     * @param pattern  "yyyy/MM/dd-HH:mm:ss"
     * @return 时间字符串
     */
    public static String getCuDateTime(String pattern){
//        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String str;
        str = formatter.format(curDate);
        cuTime = str;
        return str;
    }
    public static String getCuWeekDay(){

//        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("EE",Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间
        String str;
        str = formatter.format(curDate);
        cuWeekDay = str;
        return str;
    }
}
