package com.wmenjoy.utils.cache;

public interface DoubleObjectFactory<FK, SK, V> {
    /**
     * 生产出指定的对象
     * 
     * @param param
     * @return
     */
    public V get(final FK firstKey, final SK secondKey);
}
