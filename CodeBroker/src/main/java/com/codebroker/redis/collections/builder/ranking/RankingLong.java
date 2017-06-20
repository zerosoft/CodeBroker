package com.codebroker.redis.collections.builder.ranking;

import com.codebroker.redis.collections.Ranking;

public class RankingLong implements Ranking<Long> {

	private String name;

	private Long point;

	public RankingLong(String key, String value) {
		this.name = key;
		this.point = Long.valueOf(value);
	}

	public RankingLong(String key, Long point) {
		this.name = key;
		this.point = point;
	}

	@Override
	public Long getPoints() {
		return point;
	}

	@Override
	public String getName() {
		return name;
	}

}
