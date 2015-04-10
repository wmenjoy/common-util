package com.wmenjoy.utils.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommonObjectCache<K, V> implements ObjectCache<K, V> {
    /**
     * 用于缓存对象
     */
    ConcurrentMap<K, V> cache = new ConcurrentHashMap<K, V>();

    @Override
    public V get(final K key) {
        if (key == null) {
            return null;
        }

        V v = this.cache.get(key);

        if (v == null) {
            v = this.getObjectFactory().get(key);
            if (v == null) {
                return null;
            }
            final V returnValue = this.cache.putIfAbsent(key, v);
            /**
             * 保障用户读取的是同一个对象
             */
            if (returnValue != null) {
                v = returnValue;
            }
        }
        return v;
    }

    private final ObjectFactory<K, V> factory;

    public CommonObjectCache(final ObjectFactory<K, V> factory) {
        this.factory = factory;
    }

    public ObjectFactory<K, V> getObjectFactory() {
        return this.factory;
    }

    @Override
    @Deprecated
    public V get(final K key, final int expiration) {
        return null;
    }
}
