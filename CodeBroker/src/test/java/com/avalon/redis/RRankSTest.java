package com.avalon.redis;

import org.junit.Before;

import com.codebroker.redis.RedisService;
import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.util.PropertiesWrapper;

import jodd.props.Props;
import redis.clients.jedis.Jedis;

public class RRankSTest {
	private RankingStructure<Double> ranks;
	
	@Before
	public void init() {
		Props properties = new Props();
		properties.setValue("redis.isPool", "true");
		properties.setValue("redis.url", "192.168.0.199");
		properties.setValue("redis.port", "32768");
		properties.setValue("redis.password", "");
		PropertiesWrapper propertiesWrapper = new PropertiesWrapper(properties);
		RedisService redisService=new RedisService();
		redisService.init(propertiesWrapper);
		Jedis jedis = redisService.getJedis();
		ranks = RedisStrutureBuilder.ofRanking(jedis).withNameSpace("ranks").buildDouble();
	}
}
