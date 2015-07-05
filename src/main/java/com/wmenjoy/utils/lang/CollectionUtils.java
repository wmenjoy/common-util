package com.wmenjoy.utils.lang;

import java.util.HashSet;
import java.util.Set;

public abstract class CollectionUtils {


	public static <T> Set<T> newHashSet(final T... ts) {

		final Set<T> resultSet = new HashSet<T>();
		if (ts == null) {
			return new HashSet<T>();
		} else {

			for (final T t : ts) {
				resultSet.add(t);
			}
		}

		return resultSet;
	}
}
