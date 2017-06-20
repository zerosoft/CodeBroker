package com.codebroker.redis.collections.builder.ranking;

import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.AbstractExpirable;
import com.codebroker.redis.collections.builder.RedisUtils;
import com.codebroker.redis.collections.builder.score.ScorePointDouble;

import redis.clients.jedis.Jedis;

public class RankingStructureDouble extends AbstractExpirable<Double> implements RankingStructure<Double> {

	public RankingStructureDouble(Jedis jedis, String nameSpace) {
		super(jedis, Double.class, nameSpace);
	}

	@Override
	public ScoresPoint<Double> create(String key) {
		return new ScorePointDouble(jedis, RedisUtils.createKeyWithNameSpace(key, nameSpace));
	}

	@Override
	public void delete(String key) {
		jedis.del(RedisUtils.createKeyWithNameSpace(key, nameSpace));
	}

}
