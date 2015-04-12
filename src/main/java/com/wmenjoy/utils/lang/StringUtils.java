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

}
