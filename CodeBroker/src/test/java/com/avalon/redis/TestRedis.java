package com.avalon.redis;

import java.util.List;

import org.junit.Assert;

import com.codebroker.redis.RedisService;
import com.codebroker.redis.collections.Ranking;
import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.util.PropertiesWrapper;

import jodd.props.Props;
import redis.clients.jedis.Jedis;

public class TestRedis {
	public static void main(String[] args) {
		Props properties = new Props();
		properties.setValue("redis.isPool", "true");
		properties.setValue("redis.url", "192.168.0.199");
		properties.setValue("redis.port", "32768");
		properties.setValue("redis.password", "");
		PropertiesWrapper propertiesWrapper = new PropertiesWrapper(properties);
		RedisService redisService=new RedisService();
		redisService.init(propertiesWrapper);
		Jedis jedis = redisService.getJedis();
//		  SetStructure<String> build = RedisStrutureBuilder.ofSet(jedis, String.class).withNameSpace("setKey1").build();
//		  Set<String> createSet = build.createSet("tset1");
//		  createSet.add("3");
//		  createSet.add("4");
//		 jedis.close();
//		  RankingStructure<Long> buildLong= RedisStrutureBuilder.ofRanking(jedis).withNameSpace("Rank").buildLong();
//		  ScoresPoint<Long> create = buildLong.create("long");
//		  create.increment("avx2", 10L);
//		  create.increment("asx3", 20L);
//		  create.increment("affx4", 5L);
		  
		 jedis.lpush("logstash", "{key 234:'中国'}","{name2344:'中国2'}");
		  
		 RankingStructure<Double>  laughThings = RedisStrutureBuilder.ofRanking(jedis).withNameSpace("laughThings").buildDouble();
		 ScoresPoint<Double> sneeze = laughThings.create("sneeze");
			
		sneeze.decrement("Newton", 24.2424242424);
		sneeze.increment("Erish", 25.9);
		sneeze.increment("David", 924.786);
		sneeze.increment("Zero", 925.786);
		
		List<Ranking<Double>> ranking = sneeze.getRanking();
		
		Assert.assertEquals(ranking.get(0).getName(), "David");
		Assert.assertEquals(ranking.get(1).getName(), "Erish");
		Assert.assertEquals(ranking.get(2).getName(), "Newton");
		
		Assert.assertEquals(ranking.get(0).getPoints(), Double.valueOf(924.786));
		Assert.assertEquals(ranking.get(1).getPoints(), Double.valueOf(25.9));
		Assert.assertEquals(ranking.get(2).getPoints(), Double.valueOf(24.2424242424));
	}
}
