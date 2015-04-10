package com.wmenjoy.utils.lang;

public abstract class StringUtils {

    /**
     *
     * @param fieldDesc
     * @return
     */
    public static boolean isBlank(final String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * " " false "" false null false;
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    public static String trim(final String value) {
        return value == null ? null : value.trim();

    }

    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return isEmpty(ts) ? null : ts;

    }

    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 字符串的null常量
     * */
    public static final String EMPTY = "";

    /***
     *
     * "" true <br>
     * " " false <br>
     * null true <br>
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str) {
        return (str == null) || (str.length() == 0);
    }

    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    public static String[] split(final String msg, final String sep) {

        if (msg == null) {
            return (new String[0]);
        }
        return msg.split(sep);

    }

    public static boolean startsWith(final String line, final String headStr) {
        if ((line == null) || (headStr == null) || (line.length() < headStr.length())) {
            return false;
        }

        return line.startsWith(headStr);
    }

    public static boolean equals(final String str1, final String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(final String str1, final String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }
}
