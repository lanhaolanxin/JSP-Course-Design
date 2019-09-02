package com.xt.www.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class DateUtils {

    /**
     * 将日期根据指定根式转成字符串
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format){
        if(date == null){
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static Date toDate(String stringDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        Date date = sdf.parse(stringDate);
        return date;
    }


}
