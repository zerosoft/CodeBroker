package com.codebroker.redis.collections.builder.impl;

import com.codebroker.redis.collections.ListStructure;
import com.codebroker.redis.collections.builder.AbstractExpirable;
import com.codebroker.redis.collections.builder.RedisList;
import com.codebroker.redis.collections.builder.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

public class ListStructureImpl<T> extends AbstractExpirable<T> implements ListStructure<T> {

    public ListStructureImpl(Jedis jedis, Class<T> clazz, String nameSpace) {
        super(jedis, clazz, nameSpace);
    }

    @Override
    public List<T> get(String key) {
        return new RedisList<>(jedis, clazz, RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

    @Override
    public void delete(String key) {
        jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));

    }

}
