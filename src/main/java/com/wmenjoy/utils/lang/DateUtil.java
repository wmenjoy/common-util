package com.wmenjoy.utils.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.wmenjoy.utils.log.ExceptionLogger;

/**
 * 专门处理日期的工具类
 *
 * @author jinliang.liu
 *
 */
public abstract class DateUtil {

    /**
     * 默认时区
     */
    private final static TimeZone timeZone = TimeZone.getTimeZone("GMT+08:00");

    public static final String FORMAT_DEFAULT = "yyyy-MM-dd";
    public static final String FORMAT_ALL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_ALL_L = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_CHINESE = "yyyy年MM月dd日";
    public static final String FORMAT_ALL_M = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_SMALL = "yyyyMMddHHmmss";
    public static final String FORMAT_DEFAULT_SMALL = "yyyyMMdd";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_TIME_HOUR = "HH";
    public static final String FORMAT_TIME_MINUTE = "mm";
    public static final String FORMAT_TIME_SECOND = "ss";
    public static final String FORMAT_CHINESE_REMOVE_YEAR = "MM月dd日";
    public static final String FORMAT_SMALL_NOT_SECOND = "yyyyMMddHHmm";

    /**
     * 将一个字符串的日期描述转换为java.util.Date对象
     *
     * @param strDate 字符串的日期描述
     * @param format 字符串的日期格式，比如:“yyyy-MM-dd HH:mm”
     * @return 字符串转换的日期对象java.util.Date
     * @throws ParseException
     */
    public static Date getDate(final String strDate, final String format) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(timeZone);

        try {
            return formatter.parse(strDate);
        } catch (final ParseException e) {
            ExceptionLogger.error(e);
            return null;
        }
    }

}
