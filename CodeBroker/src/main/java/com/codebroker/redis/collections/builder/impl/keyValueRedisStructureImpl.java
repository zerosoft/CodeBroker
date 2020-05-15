package com.codebroker.redis.collections.builder.impl;

import com.codebroker.redis.collections.builder.RedisUtils;
import com.codebroker.redis.collections.keyValueRedisStructure;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class keyValueRedisStructureImpl<T> implements keyValueRedisStructure<T> {

    private Class<T> clazz;

    private Jedis jedis;

    private Gson gson;

    private String nameSpace;

    private int ttlSeconds;

    public keyValueRedisStructureImpl(Jedis jedis, Class<T> clazz, String nameSpace, int ttlSeconds) {
        this.clazz = clazz;
        this.nameSpace = nameSpace;
        this.ttlSeconds = ttlSeconds;
        this.jedis = jedis;
        gson = new Gson();
    }

    @Override
    public Optional<T> get(String key) {
        String value = jedis.get(RedisUtils.createKeyWithNameSpace(key, nameSpace));
        if (StringUtils.isNotBlank(value)) {
            return Optional.ofNullable(gson.fromJson(value, clazz));
        }
        return Optional.empty();
    }

    @Override
    public List<Optional<T>> multiplesGet(Iterable<String> keys) {
        Objects.requireNonNull(keys);
        List<Optional<T>> elements = new ArrayList<>();
        for (String key : keys) {
            T bean = (T) get(key);
            if (bean != null) {
                elements.add(Optional.of(bean));
            }else {
                elements.add(Optional.empty());
            }
        }
        return elements;
    }

    @Override
    public void delete(String key) {
        jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

    @Override
    public void set(String key, T bean) {
        Objects.requireNonNull(bean, "The object to set in KeyValueStructure cannot be null");
        String valideKey = RedisUtils.createKeyWithNameSpace(key, nameSpace);
        jedis.set(valideKey, gson.toJson(bean));
        if (ttlSeconds != 0) {
            jedis.expire(valideKey, ttlSeconds);
        }
    }

}
