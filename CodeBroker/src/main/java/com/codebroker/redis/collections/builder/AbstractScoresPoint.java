package com.codebroker.redis.collections.builder;

import com.codebroker.redis.collections.Ranking;
import com.codebroker.redis.collections.ScoresPoint;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;

public abstract class AbstractScoresPoint<T extends Number> implements ScoresPoint<T> {

    protected String keyWithNameSpace;

    protected Jedis jedis;

    public AbstractScoresPoint(Jedis jedis, String keyWithNameSpace) {
        this.keyWithNameSpace = keyWithNameSpace;
        this.jedis = jedis;
    }

    @Override
    public void remove(String name) {
        jedis.zrem(keyWithNameSpace, name);
    }

    @Override
    public int size() {
        return jedis.zcard(keyWithNameSpace).intValue();
    }

    @Override
    public List<Ranking<T>> top(int top) {
        return range(0, top - 1);
    }

    @Override
    public List<Ranking<T>> last(int top) {
        long start = size() - top;
        long end = size() - 1;
        return range(start, end);
    }

    @Override
    public List<Ranking<T>> getRanking() {
        return range(0, size() - 1);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyWithNameSpace);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (AbstractScoresPoint.class.isInstance(obj)) {
            AbstractScoresPoint otherRedis = AbstractScoresPoint.class.cast(obj);
            return Objects.equals(otherRedis.keyWithNameSpace, keyWithNameSpace);
        }
        return false;
    }

}
