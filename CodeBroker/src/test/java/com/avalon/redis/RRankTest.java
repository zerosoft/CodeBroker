package com.avalon.redis;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codebroker.redis.RedisService;
import com.codebroker.redis.collections.Ranking;
import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.util.PropertiesWrapper;

import jodd.props.Props;
import redis.clients.jedis.Jedis;

public class RRankTest {
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
	
	@Test
	public void shouldAddAndRemoveValue() {
		ScoresPoint<Double> sneeze = ranks.create("ranks");
		
		sneeze.increment("Zero", 24.25262);
		sneeze.increment("han", 235.9);
		sneeze.increment("lee", 724.386);
		
		List<Ranking<Double>> ranking = sneeze.getRanking();
		
		Assert.assertEquals(ranking.get(0).getName(), "lee");
		Assert.assertEquals(ranking.get(1).getName(), "han");
		Assert.assertEquals(ranking.get(2).getName(), "Zero");
		
		Assert.assertEquals(ranking.get(0).getPoints(), Double.valueOf(724.386));
		Assert.assertEquals(ranking.get(1).getPoints(), Double.valueOf(235.9));
		Assert.assertEquals(ranking.get(2).getPoints(), Double.valueOf(24.25262));
		
	}
	
	@After
	public void dispose() {
		ranks.delete("ranks");
	}
}
