package com.wmenjoy.utils.cache;

public interface ObjectFactory<P, V> {
    /**
     * 生产出指定的对象
     * 
     * @param param
     * @return
     */
    public V get(final P param);
}
