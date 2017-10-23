package com.codebroker.redis.collections.builder;

import com.codebroker.redis.collections.CountStructure;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Objects;

public class CountStrutureLong implements CountStructure<Long> {

    private String keyWithNameSpace;

    private Jedis jedis;

    public CountStrutureLong(Jedis jedis, String keyWithNameSpace) {
        this.jedis = jedis;
        this.keyWithNameSpace = keyWithNameSpace;
    }

    @Override
    public Long get() {
        String value = jedis.get(keyWithNameSpace);
        if (StringUtils.isNotBlank(value)) {
            return Long.valueOf(value);
        }
        return 0L;
    }

    @Override
    public Long increment() {
        return jedis.incr(keyWithNameSpace);
    }

    @Override
    public Long increment(Long count) {
        return jedis.incrBy(keyWithNameSpace, count);
    }

    @Override
    public Long decrement() {
        return jedis.decr(keyWithNameSpace);
    }

    @Override
    public Long decrement(Long count) {
        return jedis.decrBy(keyWithNameSpace, count);
    }

    @Override
    public void delete() {
        jedis.del(keyWithNameSpace);
    }

    @Override
    public void expires(int ttlSeconds) {
        jedis.ttl(keyWithNameSpace);
    }

    @Override
    public void persist() {
        jedis.persist(keyWithNameSpace);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyWithNameSpace);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (CountStrutureLong.class.isInstance(obj)) {
            CountStrutureLong otherRedis = CountStrutureLong.class.cast(obj);
            return Objects.equals(otherRedis.keyWithNameSpace, keyWithNameSpace);
        }
        return false;
    }

}
