package com.codebroker.redis.collections;

import java.util.List;

public interface ScoresPoint<T extends Number> {

	void initialPoint(String name, T value);

	T increment(String field, T value);

	T decrement(String field, T value);

	void remove(String field);

	int size();

	List<Ranking<T>> range(long initial, long end);

	List<Ranking<T>> top(int top);

	List<Ranking<T>> last(int top);

	List<Ranking<T>> getRanking();
}
