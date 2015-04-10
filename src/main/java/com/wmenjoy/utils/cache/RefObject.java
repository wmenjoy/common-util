package com.wmenjoy.utils.cache;

import java.util.Date;

/**
 * 可更新Object
 *
 * @author jinliang.liu
 *
 * @param <V>
 */
public class RefObject<V> {
    private volatile V value;
    private volatile long storeTime;

    RefObject(final V value) {
        this.value = value;
        this.storeTime = (new Date()).getTime();
    }

    public boolean isExpired(final long expiration) {
        return ((new Date()).getTime() - this.storeTime) > expiration;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(final V value) {
        this.value = value;
        this.storeTime = (new Date()).getTime();
    }
}
