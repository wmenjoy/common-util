package com.wmenjoy.utils.lang;

import java.util.Collection;

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
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     * <p/>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
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

    /**
     * Check that the given CharSequence is neither <code>null</code> nor of
     * length 0. Note: Will return <code>true</code> for a CharSequence that
     * purely consists of whitespace.
     * <p/>
     * <p/>
     * <pre>
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the CharSequence to check (may be <code>null</code>)
     * @return <code>true</code> if the CharSequence is not null and has length
     * @see #hasText(String)
     */
    public static boolean hasLength(final CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given String is neither <code>null</code> nor of length 0.
     * Note: Will return <code>true</code> for a String that purely consists of
     * whitespace.
     *
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not null and has length
     * @see #hasLength(CharSequence)
     */
    public static boolean hasLength(final String str) {
        return hasLength((CharSequence) str);
    }


    /**
     * Check whether the given CharSequence has actual text. More specifically,
     * returns <code>true</code> if the string not <code>null</code>, its length
     * is greater than 0, and it contains at least one non-whitespace character.
     * <p/>
     * <p/>
     * <pre>
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
     * @param str the CharSequence to check (may be <code>null</code>)
     * @return <code>true</code> if the CharSequence is not <code>null</code>,
     * its length is greater than 0, and it does not contain whitespace
     * only
     * @see Character#isWhitespace
     */
    public static boolean hasText(final CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given String has actual text. More specifically,
     * returns <code>true</code> if the string not <code>null</code>, its length
     * is greater than 0, and it contains at least one non-whitespace character.
     *
     * @param str the String to check (may be <code>null</code>)
     * @return <code>true</code> if the String is not <code>null</code>, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText(final String str) {
        return hasText((CharSequence) str);
    }

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
        if ((line == null) || (headStr == null)
                || (line.length() < headStr.length())) {
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

    /**
     * join string.
     *
     * @param array
     *            String array.
     * @return String.
     */
    public static String join(final String[] array) {
        if (array.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array
     *            String array.
     * @param split
     *            split
     * @return String.
     */
    public static String join(final String[] array, final char split) {
        if (array.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(split);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    /**
     * join string like javascript.
     *
     * @param array
     *            String array.
     * @param split
     *            split
     * @return String.
     */
    public static String join(final String[] array, final String split) {
        if (array.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(split);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String join(final Collection<String> coll, final String split) {
        if (coll.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (final String s : coll) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(split);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static boolean containsIgnoreCase(final String url, final String string) {
        // TODO Auto-generated method stub
        return false;
    }

    public static boolean contains(final String url, final String string) {
        // TODO Auto-generated method stub
        return false;
    }

}
