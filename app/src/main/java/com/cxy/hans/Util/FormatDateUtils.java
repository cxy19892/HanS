package com.cxy.hans.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hasee on 2017/11/3.
 */
public class FormatDateUtils
{
    public static String getCurrentDate(String format) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将字符串类型的时间格式转换为需要的时间格式
     *
     * @param getdate 时间
     * @param Format1 传入的使劲按格式
     * @param toFormat2 需要的时间格式
     * @return
     */
    public static String ExchangeTimeformat(String getdate, String Format1, String toFormat2) {
        DateFormat fmt = new SimpleDateFormat(Format1);
        DateFormat fmt2 = new SimpleDateFormat(toFormat2);
        try {
            Date date = fmt.parse(getdate);
            return fmt2.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    //获取传入时间的时间点
    public static String getTime(long time, String fomart) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(fomart);

            Date curDate = new Date(time);

            return formatter.format(curDate);
        } catch (Exception e) {
            return "";
        }
    }
}
