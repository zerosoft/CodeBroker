package com.codebroker.redis.collections;

/**
 * 排行
 * 
 * @author xl
 *
 * @param <T>
 */
public interface Ranking<T extends Number> {

	T getPoints();

	String getName();
}
