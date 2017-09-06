package com.codebroker.core.entities;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.thrift.TException;

import com.codebroker.api.IUser;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;

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

	private final IObject iObject = CObjectLite.newInstance();

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

		ActorMessage actorMessage = new ActorMessage();
		ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
		actorMessage.messageRaw = byteArrayPacket.toByteBuffer();

		byte[] bytes = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.USER_SEND_PACKET_TO_IOSESSION,actorMessage);
		getActorRef().tell(bytes, ActorRef.noSender());
	}

	@Override
	public void disconnect() {
		try {
			getActorRef().tell(ThriftSerializerFactory.getTbaseMessage(Operation.USER_DISCONNECT), ActorRef.noSender());
		} catch (TException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnected() {
		try {
			byte[] tbaseMessage = ThriftSerializerFactory.getTbaseMessage(Operation.USER_IS_CONNECTED);
			return AkkaMediator.getCallBak(getActorRef(),tbaseMessage);
		} catch (Exception e) {
			return false;
		}
	}

	public void rebindIoSession(ActorRef actorRef) {
		try {
			getActorRef().tell(ThriftSerializerFactory.getTbaseMessage(Operation.USER_REUSER_BINDUSER_IOSESSION_ACTOR), ActorRef.noSender());
		} catch (TException e) {
			e.printStackTrace();
		}

	}

	@Override
	public IObject getIObject() {
		return iObject;
	}



}
