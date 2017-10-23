package com.codebroker.redis.collections.builder.score;

import com.codebroker.redis.collections.Ranking;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.AbstractScoresPoint;
import com.codebroker.redis.collections.builder.ranking.RankingLong;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScorePointLong extends AbstractScoresPoint<Long> implements ScoresPoint<Long> {

    public ScorePointLong(Jedis jedis, String keyWithNameSpace) {
        super(jedis, keyWithNameSpace);
    }

    @Override
    public void initialPoint(String field, Long value) {
        jedis.zadd(keyWithNameSpace, value.doubleValue(), field);

    }

    @Override
    public Long increment(String field, Long value) {
        return jedis.zincrby(keyWithNameSpace, value.doubleValue(), field).longValue();
    }

    @Override
    public Long decrement(String field, Long value) {
        return increment(field, -value);
    }

    @Override
    public List<Ranking<Long>> range(long start, long end) {
        List<Ranking<Long>> topRanking = new ArrayList<>();
        Set<Tuple> scores = jedis.zrevrangeWithScores(keyWithNameSpace, start, end);
        for (Tuple tuple : scores) {
            topRanking.add(new RankingLong(tuple.getElement(), (long) tuple.getScore()));
        }
        return topRanking;
    }

}
