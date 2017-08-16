package com.avalon.redis;

import java.util.List;
import java.util.Set;

import com.codebroker.redis.RedisService;
import com.codebroker.redis.collections.ListStructure;
import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.ScoresPoint;
import com.codebroker.redis.collections.SetStructure;
import com.codebroker.redis.collections.builder.buider.RankingStructureBuilder;
import com.codebroker.redis.collections.builder.buider.RedisStrutureBuilder;
import com.codebroker.redis.collections.builder.buider.SetStructureBuilder;
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
		  
	}
}
