package com.codebroker.redis.collections.builder.buider;

import org.apache.commons.lang3.StringUtils;

import com.codebroker.redis.collections.RankingStructure;
import com.codebroker.redis.collections.builder.ranking.RankingStructureDouble;
import com.codebroker.redis.collections.builder.ranking.RankingStructureLong;
import com.codebroker.redis.collections.exception.RedisBuilderException;

import redis.clients.jedis.Jedis;

public class RankingStructureBuilder {

	private String nameSpace;

	private Jedis jedis;

	RankingStructureBuilder() {

	}

	public RankingStructureBuilder(Jedis jedis) {
		this.jedis = jedis;
	}

	public RankingStructureBuilder withNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
		return this;
	}

	public RankingStructure<Long> buildLong() {
		if (StringUtils.isBlank(nameSpace)) {
			throw new RedisBuilderException("The nameSpace must be specified");
		}
		return new RankingStructureLong(jedis, nameSpace);
	}

	public RankingStructure<Double> buildDouble() {
		if (StringUtils.isBlank(nameSpace)) {
			throw new RedisBuilderException("Oxii: The nameSpace must be specified");
		}
		return new RankingStructureDouble(jedis, nameSpace);
	}
}
