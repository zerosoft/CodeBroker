package com.codebroker.redis.collections;

import java.util.Queue;

/**
 * 队列
 * 
 * @author xl
 *
 * @param <T>
 */
public interface QueueStructure<T> extends Expirable {

	Queue<T> get(String key);

	void delete(String key);

}
