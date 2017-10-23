package com.codebroker.redis.collections;

import java.util.Queue;

/**
 * 队列
 *
 * @param <T>
 * @author xl
 */
public interface QueueStructure<T> extends Expirable {

    Queue<T> get(String key);

    void delete(String key);

}
