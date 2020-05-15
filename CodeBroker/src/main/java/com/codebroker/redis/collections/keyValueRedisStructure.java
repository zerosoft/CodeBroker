package com.codebroker.redis.collections;

import java.util.List;
import java.util.Optional;

/**
 * 键值对
 *
 * @param <T>
 * @author xl
 */
public interface keyValueRedisStructure<T> {

    Optional<T> get(String key);

    void set(String key, T bean);

    List<Optional<T>> multiplesGet(Iterable<String> keys);

    void delete(String key);
}
