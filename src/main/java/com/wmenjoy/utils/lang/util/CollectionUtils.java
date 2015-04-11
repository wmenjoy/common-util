package com.wmenjoy.utils.lang.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtils {

	public static Map<String, String> toStringMap(final String... pairs) {
		final Map<String, String> parameters = new HashMap<String, String>();
		if (pairs.length > 0) {
			if (pairs.length % 2 != 0) {
				throw new IllegalArgumentException("pairs must be even.");
			}
			for (int i = 0; i < pairs.length; i = i + 2) {
				parameters.put(pairs[i], pairs[i + 1]);
			}
		}
		return parameters;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> toMap(final Object... pairs) {
		final Map<K, V> ret = new HashMap<K, V>();
		if (pairs == null || pairs.length == 0) {
			return ret;
		}

		if (pairs.length % 2 != 0) {
			throw new IllegalArgumentException(
					"Map pairs can not be odd number.");
		}
		final int len = pairs.length / 2;
		for (int i = 0; i < len; i++) {
			ret.put((K) pairs[2 * i], (V) pairs[2 * i + 1]);
		}
		return ret;
	}

	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.size() == 0;
	}

	public static boolean isNotEmpty(final Collection<?> collection) {
		return collection != null && collection.size() > 0;
	}

	public static boolean mapEquals(final Map<?, ?> map1, final Map<?, ?> map2) {
		// 地址一样，或者两个都是null，那么返回为true
		if (map1 == map2) {
			return true;
		}

		if (map1 == null || map2 == null) {
			return false;
		}

		if (map1.size() != map2.size()) {
			return false;
		}
		for (final Map.Entry<?, ?> entry : map1.entrySet()) {
			final Object key = entry.getKey();
			final Object value1 = entry.getValue();
			final Object value2 = map2.get(key);
			if (!objectEquals(value1, value2)) {
				return false;
			}
		}
		return true;
	}

	private static boolean objectEquals(final Object obj1, final Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		return obj1.equals(obj2);
	}

	public static List<String> join(final Map<String, String> map,
			final String separator) {
		if (map == null) {
			return null;
		}
		final List<String> list = new ArrayList<String>();
		if (map == null || map.size() == 0) {
			return list;
		}
		for (final Map.Entry<String, String> entry : map.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (value == null || value.length() == 0) {
				list.add(key);
			} else {
				list.add(key + separator + value);
			}
		}
		return list;
	}

	public static Map<String, String> split(final List<String> list,
			final String separator) {
		if (list == null) {
			return null;
		}
		final Map<String, String> map = new HashMap<String, String>();
		if (list == null || list.size() == 0) {
			return map;
		}
		for (final String item : list) {
			final int index = item.indexOf(separator);
			if (index == -1) {
				map.put(item, "");
			} else {
				map.put(item.substring(0, index), item.substring(index + 1));
			}
		}
		return map;
	}

	public static Map<String, Map<String, String>> splitAll(
			final Map<String, List<String>> list, final String separator) {
		if (list == null) {
			return null;
		}
		final Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		for (final Map.Entry<String, List<String>> entry : list.entrySet()) {
			result.put(entry.getKey(), split(entry.getValue(), separator));
		}
		return result;
	}

	public static Map<String, List<String>> joinAll(
			final Map<String, Map<String, String>> map, final String separator) {
		if (map == null) {
			return null;
		}
		final Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (final Map.Entry<String, Map<String, String>> entry : map
				.entrySet()) {
			result.put(entry.getKey(), join(entry.getValue(), separator));
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> sort(final List<T> list) {
		if (list != null && list.size() > 0) {
			Collections.sort((List) list);
		}
		return list;
	}

}
