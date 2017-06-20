package com.codebroker.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.codebroker.api.IoSession;
import com.codebroker.jmx.IoMonitorControl;

public class IoMonitorImpl implements IoMonitorControl {

	private Map<Long, IoSession> sessionMap = new ConcurrentHashMap<Long, IoSession>();

	@Override
	public boolean disConnect(long sessionId) {
		return sessionMap.remove(sessionId) != null;
	}

	@Override
	public int getUnbindingSessionNum() {
		return 0;
	}

	@Override
	public int getBindingSessionNum() {
		return sessionMap.size();
	}

	@Override
	public String getBindingSessionInfo(int index, int limit) {
		// String string = component.getBindingSessionInfo(index,limit);
		return "";
	}

	@Override
	public String getUnBindingSessionInfo(int index, int limit) {
		// String string = component.getUnBindingSessionInfo(index,limit);
		return "";
	}

}
