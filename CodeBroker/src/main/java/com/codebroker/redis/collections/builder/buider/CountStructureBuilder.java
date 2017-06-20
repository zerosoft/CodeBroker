package com.codebroker.redis.collections.builder.buider;

import org.apache.commons.lang3.StringUtils;

import com.codebroker.redis.collections.CountStructure;
import com.codebroker.redis.collections.builder.CountStructureDouble;
import com.codebroker.redis.collections.builder.CountStrutureLong;
import com.codebroker.redis.collections.builder.RedisUtils;
import com.codebroker.redis.collections.exception.RedisBuilderException;

import redis.clients.jedis.Jedis;

public class CountStructureBuilder {

	private String nameSpace;

	private String key;

	private Jedis jedis;

	CountStructureBuilder(Jedis jedis) {
		this.jedis = jedis;

	}

	public CountStructureBuilder withNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
		return this;
	}

	public CountStructure<Long> buildLong() {
		if (StringUtils.isBlank(nameSpace)) {
			throw new RedisBuilderException("The nameSapce must be informed");
		}
		if (StringUtils.isBlank(key)) {
			throw new RedisBuilderException("The key must be informed");
		}
		return new CountStrutureLong(jedis, RedisUtils.createKeyWithNameSpace(key, nameSpace));
	}

	public CountStructureBuilder withKey(String key) {
		this.key = key;
		return this;
	}

	public CountStructure<Double> buildDouble() {
		if (StringUtils.isBlank(nameSpace)) {
			throw new RedisBuilderException("The nameSapce must be informed");
		}
		if (StringUtils.isBlank(key)) {
			throw new RedisBuilderException("The key must be informed");
		}
		return new CountStructureDouble(jedis, RedisUtils.createKeyWithNameSpace(key, nameSpace));
	}

}
