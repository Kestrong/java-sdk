package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.DatePatternEnum;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kesc
 * @since 2019/6/24
 */
public final class DateUtil {
    private static final int[] CHINESE_WEEK = new int[]{0, 7, 1, 2, 3, 4, 5, 6};


    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static Date parseDateTime(String dateTime) {
        return parse(dateTime, DatePatternEnum.YYYYMMDDHHMMSS_BYSEP);
    }

    public static Date parseDate(String date) {
        return parse(date, DatePatternEnum.YYYYMMDD_BYSEP);
    }

    public static Date parseDateTime(String dateTime, TimeZone timeZone) {
        return parse(dateTime, DatePatternEnum.YYYYMMDDHHMMSS_BYSEP.getFormat(), timeZone);
    }

    public static Date parseDate(String date, TimeZone timeZone) {
        return parse(date, DatePatternEnum.YYYYMMDD_BYSEP.getFormat(), timeZone);
    }

    public static Date parse(String date, DatePatternEnum pattern) {
        return parse(date, pattern.getFormat());
    }

    public static Date parse(String date, String pattern) {
        return parse(date, pattern, TimeZone.getDefault());
    }

    public static Date parse(String date, DatePatternEnum pattern, TimeZone timeZone) {
        return parse(date, pattern.getFormat(), timeZone);
    }

    public static Date parse(String date, String pattern, TimeZone timeZone) {
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(pattern);
        DateTimeZone dateTimeZone = timeZone == null ? DateTimeZone.getDefault() : DateTimeZone.forTimeZone(timeZone);
        return dateFormatter.withZone(dateTimeZone).parseDateTime(date).toDate();
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, TimeZone.getDefault());
    }

    public static String format(Date date, DatePatternEnum datePatternEnum) {
        return format(date, datePatternEnum.getFormat());
    }

    public static String formatDateTime(Date date) {
        return format(date, DatePatternEnum.YYYYMMDDHHMMSS_BYSEP);
    }

    public static String formatDate(Date date) {
        return format(date, DatePatternEnum.YYYYMMDD_BYSEP);
    }

    public static String formatDateTime(Date date, TimeZone timeZone) {
        return format(date, DatePatternEnum.YYYYMMDDHHMMSS_BYSEP.getFormat(), timeZone);
    }

    public static String formatDate(Date date, TimeZone timeZone) {
        return format(date, DatePatternEnum.YYYYMMDD_BYSEP.getFormat(), timeZone);
    }

    public static String format(Date date, DatePatternEnum pattern, TimeZone timeZone) {
        return format(date, pattern.getFormat(), timeZone);
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        DateTimeZone dateTimeZone = timeZone == null ? DateTimeZone.getDefault() : DateTimeZone.forTimeZone(timeZone);
        DateTime dateTime = new DateTime(date, dateTimeZone);
        return dateTime.toString(pattern);
    }

    public static Date getMinDate(Date date) {
        if (date == null) {
            return null;
        } else {
            DateTime dateTime = new DateTime(date);
            return dateTime.withTimeAtStartOfDay().toDate();
        }
    }

    public static Date getMaxDate(Date date) {
        if (date == null) {
            return null;
        } else {
            DateTime dateTime = new DateTime(date);
            return dateTime.millisOfDay().withMaximumValue().toDate();
        }
    }

    public static int betweenOfHours(Date firstDate, Date nextDate) {
        if (firstDate != null && nextDate != null) {
            LocalDateTime fist = new LocalDateTime(firstDate);
            LocalDateTime next = new LocalDateTime(nextDate);
            return Hours.hoursBetween(next, fist).getHours();
        } else {
            return 0;
        }
    }

    public static boolean inBetween(Date date, Date start, Date end) {
        if (date != null && start != null && end != null) {
            long st = start.getTime();
            long ed = end.getTime();
            long cur = date.getTime();
            return cur < ed && cur > st;
        } else {
            return false;
        }
    }

    public static boolean before(Date start, Date end) {
        DateTime startTime = new DateTime(start);
        DateTime endTime = new DateTime(end);
        return startTime.isBefore(endTime);
    }

    public static boolean after(Date start, Date end) {
        return !before(start, end);
    }

    public static String getMonthWithMaxDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMaximumValue().toString(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
    }

    public static String getYearWithMaxDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().toString(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
    }

    public static String getMonthWithMinDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().withMinimumValue().toString(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
    }

    public static String getYearWithMinDate(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.monthOfYear().withMinimumValue().toString(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
    }


    public static int betweenOfDays(Date firstDate, Date nextDate) {
        if (firstDate != null && nextDate != null) {
            LocalDateTime fist = new LocalDateTime(firstDate);
            LocalDateTime next = new LocalDateTime(nextDate);
            return Days.daysBetween(next, fist).getDays() + 1;
        } else {
            return 0;
        }
    }

    public static Date addMilliSecond(Date date, int amount) {
        return (new DateTime(date)).plusMillis(amount).toDate();
    }

    public static Date addSecond(Date date, int amount) {
        return (new DateTime(date)).plusSeconds(amount).toDate();
    }

    public static Date addMinute(Date date, int amount) {
        return (new DateTime(date)).plusMinutes(amount).toDate();
    }

    public static Date addHour(Date date, int amount) {
        return (new DateTime(date)).plusHours(amount).toDate();
    }

    public static Date addDay(Date date, int amount) {
        return (new DateTime(date)).plusDays(amount).toDate();
    }

    public static Date addWeek(Date date, int amount) {
        return (new DateTime(date)).plusWeeks(amount).toDate();
    }


    public static Date addMonth(Date date, int amount) {
        return (new DateTime(date)).plusMonths(amount).toDate();
    }

    public static Date addYear(Date date, int amount) {
        return (new DateTime(date)).plusYears(amount).toDate();
    }

    public static Date minusMilliSecond(Date date, int amount) {
        return (new DateTime(date)).minusMillis(amount).toDate();
    }

    public static Date minusSecond(Date date, int amount) {
        return (new DateTime(date)).minusSeconds(amount).toDate();
    }

    public static Date minusMinute(Date date, int amount) {
        return (new DateTime(date)).minusMinutes(amount).toDate();
    }

    public static Date minusHour(Date date, int amount) {
        return (new DateTime(date)).minusHours(amount).toDate();
    }

    public static Date minusDay(Date date, int amount) {
        return (new DateTime(date)).minusDays(amount).toDate();
    }

    public static Date minusWeek(Date date, int amount) {
        return (new DateTime(date)).minusWeeks(amount).toDate();
    }


    public static Date minusMonth(Date date, int amount) {
        return (new DateTime(date)).minusMonths(amount).toDate();
    }

    public static Date minusYear(Date date, int amount) {
        return (new DateTime(date)).minusYears(amount).toDate();
    }


    public static Date now() {
        return new Date(getCurrentTimestamp());
    }

    public static long getDistanceMills(Date one, Date two) {
        long time1 = one.getTime();
        long time2 = two.getTime();
        long diff;
        if (time1 < time2) {
            diff = time2 - time1;
        } else {
            diff = time1 - time2;
        }

        return diff;
    }

    public static Date changeDate(Date date, DatePatternEnum from, DatePatternEnum to) {
        if (null == date) {
            return null;
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(to.getFormat());
            return dateFormatter.parseDateTime((new DateTime(date)).toString(from.getFormat())).toDate();
        }
    }

    public static int getMinutesToEndDay(Date date) {
        DateTime startDate = new DateTime(date);
        DateTime endDate = (new DateTime(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth(), 23, 59, 59, 59)).plusSeconds(1);
        Minutes minutes = Minutes.minutesBetween(startDate, endDate);
        return minutes.getMinutes();
    }

    /***
     * 获取时间在月份的天数
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.getDayOfMonth();
    }

    /***
     * 月份的天数
     * @param date
     * @return
     */
    public static int daysOfMonth(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.dayOfMonth().getMaximumValue();
    }

    /**
     * 去年的最后一天
     *
     * @param date
     * @return
     */
    public static Date lastDayOfLastYear(Date date) {
        DateTime dateTime = new DateTime(date);
        DateTime lastYearDateTime = dateTime.minusYears(1);
        return lastYearDateTime.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().toDate();
    }

    /***
     * 年的第一个天
     * @param date
     * @return
     */
    public static Date firstDayOfYear(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue().toDate();
    }

    /***
     * 年的最后一个天
     * @param date
     * @return
     */
    public static Date lastDayOfYear(Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue().toDate();
    }

    /**
     * 获取从传入日期间隔中包含一年中的周数
     */
    public static List<String> getBetweenWeeks(Date start, Date end) {
        if (end.before(start)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        Format f = new SimpleDateFormat(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
        while (f.format(start).compareTo(f.format(end)) <= 0) {
            list.add(getWeekOfYear(start));
            start = DateUtil.addDay(start, 7);
        }
        return list;
    }

    public static String getWeekOfYear(Date date) {
        if (date == null) {
            return null;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.getYear() + StringUtil.MINUS + dateTime.getWeekOfWeekyear();
    }

    /**
     * 获取从传入日期间隔中包含一年中的月份数
     */
    public static List<String> getBetweenMonth(Date start, Date end) {
        if (end.before(start)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        Format f = new SimpleDateFormat(DatePatternEnum.YYYYMM_BYSEP.getFormat());
        while (f.format(start).compareTo(f.format(end)) <= 0) {
            list.add(f.format(start));
            start = DateUtil.addMonth(start, 1);
        }
        return list;
    }

    public static List<String> getBetweenDates(Date start, Date end) {
        if (end.before(start)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        Format f = new SimpleDateFormat(DatePatternEnum.YYYYMMDD_BYSEP.getFormat());
        while (f.format(start).compareTo(f.format(end)) <= 0) {
            result.add(f.format(start));
            start = DateUtil.addDay(start, 1);
        }
        return result;
    }

    public static List<String> getBetweenHours(Date start, Date end) {
        if (end.before(start)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        Format f = new SimpleDateFormat(DatePatternEnum.YYYYMMDDHH_BYSEP.getFormat());
        while (f.format(start).compareTo(f.format(end)) <= 0) {
            result.add(f.format(start));
            start = DateUtil.addHour(start, 1);
        }
        return result;
    }

    public static int getDayOfChineseWeek(Date date) {
        return CHINESE_WEEK[getDayOfWeek(date)];
    }

    public static int getDayOfWeek(Date date) {
        return new DateTime(date).getDayOfWeek();
    }
}
