package com.codebroker.redis.collections.builder.ranking;

import com.codebroker.redis.collections.Ranking;

public class RankingDouble implements Ranking<Double> {

    private String name;

    private Double point;

    public RankingDouble(String key, Double point) {
        this.name = key;
        this.point = point;
    }

    @Override
    public Double getPoints() {
        return point;
    }

    @Override
    public String getName() {
        return name;
    }

}
