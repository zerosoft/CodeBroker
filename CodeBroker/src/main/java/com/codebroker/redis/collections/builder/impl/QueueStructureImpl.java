package com.codebroker.redis.collections.builder.impl;

import com.codebroker.redis.collections.QueueStructure;
import com.codebroker.redis.collections.builder.AbstractExpirable;
import com.codebroker.redis.collections.builder.RedisQueue;
import com.codebroker.redis.collections.builder.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.Queue;

public class QueueStructureImpl<T> extends AbstractExpirable<T> implements QueueStructure<T> {

    public QueueStructureImpl(Jedis jedis, Class<T> clazz, String nameSpace) {
        super(jedis, clazz, nameSpace);
    }

    @Override
    public Queue<T> get(String key) {
        Objects.requireNonNull(key);
        return new RedisQueue<>(jedis, clazz, RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

    @Override
    public void delete(String key) {
        jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

}
