package com.codebroker.redis.collections;

/**
 * 排行
 *
 * @param <T>
 * @author xl
 */
public interface Ranking<T extends Number> {
    /**
     * 可以排序的数字
     *
     * @return
     */
    T getPoints();

    /**
     * 排行榜的KEY
     *
     * @return
     */
    String getName();
}
