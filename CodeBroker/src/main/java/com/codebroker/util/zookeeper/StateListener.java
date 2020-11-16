package com.codebroker.util.zookeeper;

public interface StateListener {

	int DISCONNECTED = 0;

	int CONNECTED = 1;

	int RECONNECTED = 2;

	void stateChanged(int connected);

}
