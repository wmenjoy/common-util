package com.wmenjoy.utils.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommonDoubleCache<FK, SK, V> implements
		DoubleObjectCache<FK, SK, V> {
	/**
	 * 用于缓存对象
	 */
	ConcurrentMap<SK, V> cache = new ConcurrentHashMap<SK, V>();

	@Override
	public V get(final FK firstKey, final SK secondKey) {
		if (firstKey == null) {
			return null;
		}

		V v = this.cache.get(secondKey);

		if (v == null) {
			v = this.getObjectFactory().get(firstKey, secondKey);
			if (v == null) {
				return null;
			}
			final V returnValue = this.cache.putIfAbsent(secondKey, v);
			/**
			 * 保障用户读取的是同一个对象
			 */
			if (returnValue != null) {
				v = returnValue;
			}
		}
		return v;
	}

	private final DoubleObjectFactory<FK, SK, V> factory;

	public CommonDoubleCache(final DoubleObjectFactory<FK, SK, V> factory) {
		this.factory = factory;
	}

	public DoubleObjectFactory<FK, SK, V> getObjectFactory() {
		return this.factory;
	}

}