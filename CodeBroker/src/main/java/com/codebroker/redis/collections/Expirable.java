package com.codebroker.redis.collections;

/**
 * 超時
 *
 * @author xl
 */
public interface Expirable {

    void expire(String key, int ttlSeconds);

    void persist(String key);
}
