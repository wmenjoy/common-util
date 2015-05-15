package com.wmenjoy.utils.cache;

/***
 * 适用于单例模式，暂不支持V对象属性的自动更新
 * 
 * @author jinliang.liu
 *
 * @param <K>
 * @param <V>
 */
public interface DoubleObjectCache<FK, SK, V> {
    /**
     * @param key 键值，不能为null，如果为null 返回为null值
     * @return 返回单例模式，如果没获取到值，会根据ObjectFactory 构造一个新的对象 (
     *         <code>值可能为null， 如果值为null 那么说明参数不正确，或者提供的构造工厂</code>)
     */
    public V get(final FK firstKey, final SK secondKey);

}