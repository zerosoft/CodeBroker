package com.codebroker.redis.collections;

import java.util.Map;

/**
 * 图
 *
 * @param <T>
 * @author xl
 */
public interface MapStructure<T> extends Expirable {

    Map<String, T> get(String key);

    void delete(String key);

}
