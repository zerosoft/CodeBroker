package com.codebroker.redis.collections.builder.impl;

import com.codebroker.redis.collections.SetStructure;
import com.codebroker.redis.collections.builder.AbstractExpirable;
import com.codebroker.redis.collections.builder.RedisSet;
import com.codebroker.redis.collections.builder.RedisUtils;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class SetStructureImpl<T> extends AbstractExpirable<T> implements SetStructure<T> {

    public SetStructureImpl(Jedis jedis, Class<T> clazz, String nameSpace) {
        super(jedis, clazz, nameSpace);
    }

    @Override
    public Set<T> createSet(String key) {
        return new RedisSet<>(jedis, clazz, RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

    @Override
    public void delete(String key) {
        jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

}
