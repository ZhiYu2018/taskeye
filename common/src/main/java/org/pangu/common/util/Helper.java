package org.pangu.common.util;


import org.pangu.common.vo.CronExpression;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static Date strToDate(String strDate){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            return simpleDateFormat.parse(strDate);
        }catch (Throwable t){
            return null;
        }
    }

    public static String getTimeAfterStr(Date date, Long second){
        long ms = 0;
        if(second != null){
            ms = second.longValue();
        }

        ms = ms * 1000L;
        if(date == null){
            date = new Date();
        }
        Date after = new Date(date.getTime() + ms);
        return dateToString(after);
    }

    public static Date getTimeBefor(Date date, Long second){
        long ms = second.longValue() * 1000L;
        if(date == null){
            date = new Date();
        }
        Date befor = new Date(date.getTime() - ms);
        return befor;
    }

    public static Date getNextDate(String expr, Date last){
        try{
            CronExpression cronExpression = new CronExpression(expr);
            if(last == null){
                last = new Date();
            }
            return cronExpression.getTimeAfter(last);
        }catch (Throwable t){
            return null;
        }
    }

    public static Date getNextTrigDate(String expr, Date last){
        try{
            CronExpression cronExpression = new CronExpression(expr);
            Date cure = new Date();
            Date next = cronExpression.getTimeAfter(last);
            while (true){
                if(next.after(last) && next.after(cure)){
                    break;
                }
                next = cronExpression.getTimeAfter(next);
            }

            return next;
        }catch (Throwable t){
            return null;
        }
    }

}
