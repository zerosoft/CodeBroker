package com.codebroker.core.monitor;

import com.codebroker.core.data.IObject;
import com.codebroker.core.proxy.NettyTcpSupervisorProxy;

import akka.actor.AbstractActor;

public class MonitorManager extends AbstractActor {

	public static final String IDENTIFY = MonitorManager.class.getSimpleName();
	
	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(IObject.class, msg->{
			if (msg.containsKey(MonitorEventType.KEY)) {
				switch (msg.getInt(MonitorEventType.KEY))
				{
				case MonitorEventType.SESSEION_ONLINE:
					NettyTcpSupervisorProxy.getInstance().addTransportNum();
					break;
				case MonitorEventType.SESSEION_OUTLINE:
					NettyTcpSupervisorProxy.getInstance().subTransportNum();
					break;
				case MonitorEventType.SESSEION_RECIVE_FLOW:
					Long sessionId=msg.getLong(MonitorEventType.SESSION_ID);
					Double flow = msg.getDouble(MonitorEventType.SESSION_FLOW);
					Long time = msg.getLong(MonitorEventType.SESSION_TIME);
					NettyTcpSupervisorProxy.getInstance().addSessionReciveFlow(sessionId,flow,time);
					break;
				case MonitorEventType.SESSEION_WRITE_FLOW:
					 sessionId=msg.getLong(MonitorEventType.SESSION_ID);
					 flow = msg.getDouble(MonitorEventType.SESSION_FLOW);
					 time = msg.getLong(MonitorEventType.SESSION_TIME);
					NettyTcpSupervisorProxy.getInstance().addSessionWriteFlow(sessionId,flow,time);
					break;
				default:
					break;
				}
			}
		}).build();
	}

}
