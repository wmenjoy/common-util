package com.wmenjoy.utils.cache;

public interface Updatable<K, V> {

    public void update(K param, V value);
}
