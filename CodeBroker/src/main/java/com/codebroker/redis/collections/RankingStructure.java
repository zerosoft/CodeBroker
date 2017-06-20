package com.codebroker.redis.collections;

public interface RankingStructure<T extends Number> extends Expirable {

	ScoresPoint<T> create(String key);

	void delete(String key);
}
