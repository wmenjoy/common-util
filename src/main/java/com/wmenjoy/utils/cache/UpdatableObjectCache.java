package com.wmenjoy.utils.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UpdatableObjectCache<K, V> implements ObjectCache<K, V>, Updatable<K, V> {
    /**
     * 用于缓存对象
     */
    /**
     * 用于缓存对象
     */
    ConcurrentMap<K, RefObject<V>> cache = new ConcurrentHashMap<K, RefObject<V>>();

    /**
     * 永久有效期的对象
     */
    @Override
    public V get(final K key) {
        if (key == null) {
            return null;
        }

        RefObject<V> refValue = this.cache.get(key);

        if (refValue == null) {
            final V value = this.getObjectFactory().get(key);
            if (value == null) {
                return null;
            }
            refValue = new RefObject<V>(value);
            final RefObject<V> returnValue = this.cache.putIfAbsent(key, refValue);
            /**
             * 保障用户读取的是同一个对象
             */
            if (returnValue != null) {
                refValue = returnValue;
            }
        }
        return refValue.getValue();
    }

    private final ObjectFactory<K, V> factory;

    public UpdatableObjectCache(final ObjectFactory<K, V> factory) {
        this.factory = factory;
    }

    public ObjectFactory<K, V> getObjectFactory() {
        return this.factory;
    }

    /***
     * 指定有效期的对象
     */
    @Override
    public V get(final K key, final int expiration) {
        if (key == null) {
            return null;
        }

        if (expiration < 5) {
            return this.getObjectFactory().get(key);
        }

        final RefObject<V> oldValue = this.cache.get(key);

        if ((oldValue == null) || oldValue.isExpired(expiration)) {
            final V value = this.getObjectFactory().get(key);
            if (value == null) {
                return null;
            }
            final RefObject<V> newValue = new RefObject<V>(value);
            final boolean result = this.cache.replace(key, oldValue, newValue);
            /**
             * 保障用户读取的是同一个对象
             */
            if (result) {
                return newValue.getValue();
            } else {
                //有可能已经超时， 重新计数
                return this.get(key, expiration);
            }
        }

        return oldValue.getValue();
    }

    @Override
    public void update(final K param, final V value) {
        final RefObject<V> refValue = this.cache.get(param);

        if (refValue != null) {
            refValue.setValue(value);
        }

    }
}
