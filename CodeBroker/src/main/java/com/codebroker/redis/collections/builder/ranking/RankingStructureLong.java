package com.codebroker.redis.collections.builder.ranking;

import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.AbstractExpirable;
import com.codebroker.redis.collections.builder.RedisUtils;
import com.codebroker.redis.collections.builder.score.ScorePointLong;
import redis.clients.jedis.Jedis;

public class RankingStructureLong extends AbstractExpirable<Long> implements RankingStructure<Long> {

    public RankingStructureLong(Jedis jedis, String nameSpace) {
        super(jedis, Long.class, nameSpace);
    }

    @Override
    public ScoresPoint<Long> create(String key) {
        return new ScorePointLong(jedis, RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

    @Override
    public void delete(String key) {
        jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));
    }

}
