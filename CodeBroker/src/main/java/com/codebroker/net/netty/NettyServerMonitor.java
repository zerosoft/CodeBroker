package com.codebroker.net.netty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.codebroker.api.IoSession;

/**
 * IoSession管理器
 * 
 * @author server
 *
 */
public class NettyServerMonitor {

	private Map<Long, IoSession> bindingSessions = new ConcurrentHashMap<>();

	public static AtomicLong sessionIds = new AtomicLong(1);

	public void addIoSession(IoSession ioSession) {
		bindingSessions.put(ioSession.getSessionId(), ioSession);
	}

	public void removeIoSession(IoSession ioSession) {
		bindingSessions.remove(ioSession.getSessionId());
	}

	public int getBindingSessionNum() {
		return bindingSessions.size();
	}

	public String getBindingSessionInfo(int index, int limit) {
		if (index > bindingSessions.size()) {
			index = 0;
		}
		int indexs = 0;
		boolean search = false;
		StringBuffer stringBuffer = new StringBuffer();
		for (Entry<Long, IoSession> entry : bindingSessions.entrySet()) {
			if (index == indexs) {
				search = true;
			}
			if (search) {
				stringBuffer.append(entry.getValue().getSessionId() + ":" + entry.getKey() + "\n");
			}
			indexs++;
			if (indexs >= index + limit) {
				break;
			}
		}
		return stringBuffer.toString();
	}

}
