package com.codebroker.redis.collections.builder.score;

import com.codebroker.redis.collections.Ranking;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.AbstractScoresPoint;
import com.codebroker.redis.collections.builder.ranking.RankingDouble;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScorePointDouble extends AbstractScoresPoint<Double> implements ScoresPoint<Double> {

    public ScorePointDouble(Jedis jedis, String keyWithNameSpace) {
        super(jedis, keyWithNameSpace);
    }

    @Override
    public void initialPoint(String field, Double value) {
        jedis.zadd(keyWithNameSpace, value, field);

    }

    @Override
    public Double increment(String field, Double value) {
        return jedis.zincrby(keyWithNameSpace, value, field);
    }

    @Override
    public Double decrement(String field, Double value) {
        return increment(field, -value);
    }

    @Override
    public List<Ranking<Double>> range(long start, long end) {
        List<Ranking<Double>> topRanking = new ArrayList<>();
        Set<Tuple> scores = jedis.zrevrangeWithScores(keyWithNameSpace, start, end);
        for (Tuple tuple : scores) {
            topRanking.add(new RankingDouble(tuple.getElement(), tuple.getScore()));
        }
        return topRanking;
    }

}
