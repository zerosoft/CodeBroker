package com.codebroker;

import org.apache.thrift.TException;
import org.junit.Test;

import com.codebroker.protocol.ThriftSerializerFactory;

public class TestEventMessage {
	ThriftSerializerFactory ThriftSerializerFactory=new ThriftSerializerFactory();
	@Test
	public void testSD() throws TException {
//		IObject object = CObject.newInstance();
//		object.putUtfString("ee", "sss");
//		object.putInt("VI", 13);
//		Event event = new Event("test", object);
//
//		
//		byte[] bytes = event.getBytes();
//		ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(bytes);
//		ByteBuffer messageRaw = actorMessage.messageRaw;
//		byte[] raw = new byte[messageRaw.remaining()];
//		messageRaw.get(raw);
//
//		Event event2 = new Event(raw);
//		System.out.println(event2.topic);
//		System.out.println(event2.message.toJson());
	}
	
//	public Event(byte[] bs) throws TException {
//	super();
//	RemoteEventMessage message=new RemoteEventMessage();
//	thriftSerializerFactory.deserialize(message, bs);
//	this.topic = message.topic;
//	byte[] bytes=new byte[message.iobject.remaining()];
//	message.iobject.get(bytes);
//	CObject cObject=CObject.newFromBinaryData(bytes);
//	this.message = cObject;
//}
//
//
//public byte[] getBytes(){
//	RemoteEventMessage remoteEventMessage=new RemoteEventMessage();
//	remoteEventMessage.topic=topic;
//	byte[] binary = message.toBinary();
//	ByteBuffer buffer=ByteBuffer.allocate(binary.length);
//	buffer.put(binary);
//	buffer.flip();
//	remoteEventMessage.iobject=buffer;
//	return thriftSerializerFactory.getActorMessageWithSubClass(Operation.EVENT_REMOTE_MESSAGE,remoteEventMessage);
//}
}
