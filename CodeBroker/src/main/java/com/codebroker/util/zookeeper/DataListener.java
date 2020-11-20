package com.codebroker.util.zookeeper;

import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

public interface DataListener {

	void dataChanged(String path, Object value, CuratorCacheListener.Type eventType);
}
