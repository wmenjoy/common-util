package com.wmenjoy.utils.security.crypto.random;

/**
 * 伪随机数生成算法
 * @author jinliang.liu
 *
 */
public interface random {
    /**
     * 真实的随机数生成算法
     * @return
     */
    public long get();
}
