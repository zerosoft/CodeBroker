package com.codebroker.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具集
 */
public class TimeUtil {
    private static long msOffset = 0L;


    public static long getMsOffset() {
        return msOffset;
    }

    public static void setMsOffset(long msOffset) {
        TimeUtil.msOffset = msOffset;
    }

    public static Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        if (getMsOffset() != 0L) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + getMsOffset());
        }
        return calendar;
    }

    public static long getMillisecond() {
        return getCalendar().getTimeInMillis() + getMsOffset();
    }

    public static int getSeconds() {
        return (int) ((getCalendar().getTimeInMillis() + getMsOffset()) / 1000L);
    }

    public static Timestamp getTimestamp() {
        Timestamp ts = new Timestamp(getMillisecond());
        return ts;
    }

    public static Date getAM0Date() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getAM0Date(Date date) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean isSameDay(long time1, long time2) {
        Calendar dt1 = getCalendar();
        Calendar dt2 = getCalendar();
        dt1.setTime(new Date(time1));
        dt2.setTime(new Date(time2));
        if ((dt1.get(Calendar.MONTH) == dt2.get(Calendar.MONTH)) && (dt1.get(Calendar.DAY_OF_MONTH) == dt2.get(Calendar.DAY_OF_MONTH))) {
            return true;
        }
        return false;
    }

    public static boolean isToday(Date date) {
        Calendar dt1 = getCalendar();
        Calendar dt2 = getCalendar();
        dt2.setTime(date);
        if ((dt1.get(Calendar.MONTH) == dt2.get(Calendar.MONTH)) && (dt1.get(Calendar.DAY_OF_MONTH) == dt2.get(Calendar.DAY_OF_MONTH))) {
            return true;
        }
        return false;
    }

    public static String getTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(getCalendar().getTime());
    }

    public static String getTimeString(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    public static String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getCalendar().getTime());
    }

    public static long getNextAM0Date() {
        Calendar calendar = getCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static int calcBetweenDays(Date startDate, Date endDate) {
        if ((startDate == null) || (endDate == null)) {
            return 0;
        }
        Date startDate0AM = getAM0Date(startDate);
        Date endDate0AM = getAM0Date(endDate);
        long v1 = startDate0AM.getTime() - endDate0AM.getTime();
        return Math.abs((int) divideAndRoundUp(v1, 8.64E7D, 0));
    }

    private static double divideAndRoundUp(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("the scale must be a positive integer or zero");
        }

        BigDecimal bd1 = new BigDecimal(v1);
        BigDecimal bd2 = new BigDecimal(v2);
        return bd1.divide(bd2, scale, 0).doubleValue();
    }
}
