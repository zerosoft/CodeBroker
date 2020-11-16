package com.codebroker.util.zookeeper;

public interface DataListener {

	void dataChanged(String path, Object value, EventType eventType);
}
