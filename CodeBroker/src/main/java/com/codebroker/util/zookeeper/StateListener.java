package com.codebroker.util.zookeeper;

/**
 * Zookeeper链接的状态监听
 */
public interface StateListener {

	int DISCONNECTED = 0;

	int CONNECTED = 1;

	int RECONNECTED = 2;

	void stateChanged(int connected);

}
