package com.codebroker.core.entities;

import java.io.Serializable;

import com.codebroker.api.IUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
import com.codebroker.core.actor.UserActor;
import com.codebroker.util.AkkaMediator;

import akka.actor.ActorRef;

/**
 * 操作代理对象
 * 
 * @author xl
 *
 */
public class User implements IUser, Serializable {

	private static final long serialVersionUID = -2129345416143459874L;

	private String userId;

	private ActorRef userRef;

	private boolean npc;

	private boolean hasUserRef;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ActorRef getUserRef() {
		return userRef;
	}

	public void setUserRef(ActorRef userRef) {
		this.userRef = userRef;
		this.hasUserRef = true;
	}

	public boolean isNpc() {
		return npc;
	}

	public void setNpc(boolean npc) {
		this.npc = npc;
	}



	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(int requestId, Object message) {
		if (hasUserRef) {
			userRef.tell(new UserActor.SendMessage(requestId,message), ActorRef.noSender());
		}

	}

	@Override
	public void disconnect() {
		if (hasUserRef) {
			userRef.tell(new UserActor.Disconnect(), ActorRef.noSender());
		}
	}

	@Override
	public boolean isConnected() {
		try {
			return AkkaMediator.getCallBak(userRef, new UserActor.IsConnected());
		} catch (Exception e) {
			return false;
		}
	}

	public void rebindIoSession(ActorRef actorRef) {
		if (hasUserRef) {
			userRef.tell(new UserActor.ReBindIoSession(actorRef), ActorRef.noSender());
		}
	}


	@Override
	public void addEventListener(String paramString, IEventListener paramIEventListener) {
		if (hasUserRef) {
			userRef.tell(new UserActor.AddEventListener(paramString,paramIEventListener), ActorRef.noSender());
		}
	}

	@Override
	public boolean hasEventListener(String paramString) {
		try {
			return AkkaMediator.getCallBak(userRef, new UserActor.HasEventListener(paramString));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void removeEventListener(String paramString) {
		if (hasUserRef) {
			userRef.tell(new UserActor.RemoveEventListener(paramString), ActorRef.noSender());
		}
		
	}

	@Override
	public void dispatchEvent(IEvent paramIEvent) {
		if (hasUserRef) {
			userRef.tell(new UserActor.DispatchEvent(paramIEvent), ActorRef.noSender());
		}
		
	}

}
