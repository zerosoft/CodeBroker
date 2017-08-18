package com.codebroker.api;

/**
 * AKKA分布式缓存
 * 
 * @author zero
 *
 */
public interface IReplicatedCache {

	public void PutInCache(String key, Object value);

	public Object GetFromCache(String key) throws Exception;

	public void Remove(String key);
}
