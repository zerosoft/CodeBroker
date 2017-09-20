package com.codebroker.protocol;

import java.nio.ByteBuffer;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.event.RemoteEventMessage;

/**
 * 序列化工厂
 * 
 * @author zero
 *
 */
public class ThriftSerializerFactory {

	private TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
	private TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());

	public byte[] getActorMessage(TBase<?, ?> base) throws TException {
		byte[] tbaseMessage = null;
		tbaseMessage = getTbaseMessage(base);
		return tbaseMessage;
	}

	public ActorMessage getActorMessage(byte[] buffer) {
		BaseByteArrayPacket packet = new BaseByteArrayPacket();
		packet.fromBinary(buffer);
		ActorMessage actorMessage = new ActorMessage();
		try {
			deserializer.deserialize(actorMessage, packet.toBinary());
		} catch (TException e) {
			e.printStackTrace();
		}
		return actorMessage;
	}

	public RemoteEventMessage getEventMessage(byte[] buffer) {
		RemoteEventMessage event = new RemoteEventMessage();
		try {
			deserializer.deserialize(event, buffer);
		} catch (TException e) {
			e.printStackTrace();
		}
		return event;
	}

	public byte[] deserializeEventMessage(RemoteEventMessage event) {
		try {
			return serializer.serialize(event);
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getActorMessageWithSubClass(Operation operation, TBase<?, ?> base) {
		byte[] tbaseMessage = null;
		ActorMessage actorMessage = new ActorMessage();
		actorMessage.op = operation;
		try {
			actorMessage.messageRaw = getTbaseByteBuffer(base);
			tbaseMessage = getTbaseMessage(actorMessage);
		} catch (TException e) {
			e.printStackTrace();
		}
		return tbaseMessage;
	}

	public byte[] getTbaseMessage(Operation operation) throws TException {
		ActorMessage message = new ActorMessage(operation);
		byte[] serialize = null;
		serialize = serializer.serialize(message);
		return serialize;
	}

	public byte[] getTbaseMessage(TBase<?, ?> message) throws TException {
		byte[] serialize = null;
		serialize = serializer.serialize(message);
		return serialize;
	}

	private ByteBuffer getTbaseByteBuffer(TBase<?, ?> message) throws TException {
		byte[] tbaseMessage = getTbaseMessage(message);
		ByteBuffer buffer = ByteBuffer.allocate(tbaseMessage.length);
		buffer.put(tbaseMessage);
		buffer.flip();
		return buffer;
	}

	public void deserialize(TBase<?, ?> base, ByteBuffer byteBuffer) throws TException {
		deserializer.deserialize(base, getBytes(byteBuffer));
	}

	public void deserialize(TBase<?, ?> base, byte[] bytes) throws TException {
		deserializer.deserialize(base, bytes);
	}

	private byte[] getBytes(ByteBuffer byteBuffer) {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return bytes;
	}

}
