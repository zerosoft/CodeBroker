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
}
