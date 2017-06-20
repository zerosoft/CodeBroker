package com.codebroker.redis.collections.builder;

import com.codebroker.redis.collections.Expirable;

import redis.clients.jedis.Jedis;

public class AbstractExpirable<T> implements Expirable {

	protected Class<T> clazz;

	protected String nameSpace;

	protected Jedis jedis;

	public AbstractExpirable(Jedis jedis, Class<T> clazz, String nameSpace) {
		this.jedis = jedis;
		this.clazz = clazz;
		this.nameSpace = nameSpace;

	}

	@Override
	public void expire(String key, int ttlSeconds) {
		jedis.expire(RedisUtils.createKeyWithNameSpace(key, nameSpace), ttlSeconds);
	}

	@Override
	public void persist(String key) {
		jedis.persist(RedisUtils.createKeyWithNameSpace(key, nameSpace));
	}

}
