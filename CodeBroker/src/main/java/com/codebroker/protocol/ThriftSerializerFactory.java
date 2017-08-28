package com.codebroker.protocol;


import java.nio.ByteBuffer;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;

/**
 * 序列化工厂
 * @author zero
 *
 */
public class ThriftSerializerFactory {
	
	private static TDeserializer deserializer=new TDeserializer(new TBinaryProtocol.Factory());
	private static TSerializer   serializer=new TSerializer(new TBinaryProtocol.Factory());

	public static byte[] getActorMessage(TBase<?, ?> base) throws TException{
		byte[] tbaseMessage=null;
		tbaseMessage = getTbaseMessage(base);
		return tbaseMessage;
	}
	
	public static ActorMessage getActorMessage(byte[] buffer){
		BaseByteArrayPacket packet=new BaseByteArrayPacket();
		packet.fromBinary(buffer);
		ActorMessage actorMessage=new ActorMessage();
		try {
			deserializer.deserialize(actorMessage, packet.toBinary());
		} catch (TException e) {
			e.printStackTrace();
		}
		return actorMessage;
	}
	
	public static byte[] getActorMessageWithSubClass(Operation operation,TBase<?, ?> base){
		byte[] tbaseMessage=null;
		ActorMessage actorMessage=new ActorMessage();
		actorMessage.op=operation;
		try {
			actorMessage.messageRaw=getTbaseByteBuffer(base);
			tbaseMessage = getTbaseMessage(actorMessage);
		} catch (TException e) {
			e.printStackTrace();
		}
		return tbaseMessage;
	}
	
	public static byte[] getTbaseMessage(Operation operation) throws TException {
		ActorMessage message=new ActorMessage(operation);
		byte[] serialize=null;
	    serialize = serializer.serialize(message);
		return serialize;
	}
	
	public static byte[] getTbaseMessage(TBase<?, ?> message) throws TException {
		byte[] serialize=null;
	    serialize = serializer.serialize(message);
		return serialize;
	}
	
	
	private static ByteBuffer getTbaseByteBuffer(TBase<?, ?> message) throws TException{
		byte[] tbaseMessage=getTbaseMessage(message);
		ByteBuffer buffer=ByteBuffer.allocate(tbaseMessage.length);
		buffer.put(tbaseMessage);
		buffer.flip();
		return buffer;
	}
	
	public static void deserialize(TBase<?, ?> base, ByteBuffer byteBuffer) throws TException{
		deserializer.deserialize(base, getBytes(byteBuffer));
	}
	
	public static void deserialize(TBase<?, ?> base, byte[] bytes) throws TException{
		deserializer.deserialize(base, bytes);
	}

	private static byte[] getBytes(ByteBuffer byteBuffer){
		byte[] bytes=new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return bytes;
	}
	
}
