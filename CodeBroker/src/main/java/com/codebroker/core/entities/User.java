package com.codebroker.core.entities;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codebroker.api.IUser;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.actor.UserActor;
import com.codebroker.util.AkkaMediator;

import akka.actor.ActorRef;

/**
 * 操作代理对象
 * 
 * @author xl
 *
 */
public class User extends EventDispatcher implements IUser, Serializable {

	private static final long serialVersionUID = -2129345416143459874L;

	private String userId;

	private boolean npc;

	private final ConcurrentMap<Object, Object> properties = new ConcurrentHashMap<Object, Object>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isNpc() {
		return npc;
	}

	public void setNpc(boolean npc) {
		this.npc = npc;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void sendMessage(int requestId, Object message) {
		getActorRef().tell(new UserActor.SendMessage(requestId, message), ActorRef.noSender());
	}

	@Override
	public void disconnect() {
		getActorRef().tell(new UserActor.Disconnect(), ActorRef.noSender());
	}

	@Override
	public boolean isConnected() {
		try {
			return AkkaMediator.getCallBak(getActorRef(), new UserActor.IsConnected());
		} catch (Exception e) {
			return false;
		}
	}

	public void rebindIoSession(ActorRef actorRef) {
		getActorRef().tell(new UserActor.ReBindIoSession(actorRef), ActorRef.noSender());
	}

	@Override
	public Object getProperty(Object key) {
		return properties.get(key);
	}

	@Override
	public void setProperty(Object key, Object value) {
		properties.put(key, value);
	}

	@Override
	public boolean containsProperty(Object key) {
		return properties.containsKey(key);
	}

	@Override
	public void removeProperty(Object key) {
		properties.remove(key);
	}

}
