package com.codebroker.api.event;

import com.codebroker.core.data.IObject;
import com.codebroker.protocol.ThriftSerializerFactory;

public class Event {
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
	public String topic;
	public IObject message;
	
	public Event(String topic, IObject message) {
		super();
		this.topic = topic;
		this.message = message;
	}


	public Event() {
		super();
	}

//	public Event(byte[] bs) throws TException {
//		super();
//		RemoteEventMessage message=new RemoteEventMessage();
//		thriftSerializerFactory.deserialize(message, bs);
//		this.topic = message.topic;
//		byte[] bytes=new byte[message.iobject.remaining()];
//		message.iobject.get(bytes);
//		CObject cObject=CObject.newFromBinaryData(bytes);
//		this.message = cObject;
//	}
//
//
//	public byte[] getBytes(){
//		RemoteEventMessage remoteEventMessage=new RemoteEventMessage();
//		remoteEventMessage.topic=topic;
//		byte[] binary = message.toBinary();
//		ByteBuffer buffer=ByteBuffer.allocate(binary.length);
//		buffer.put(binary);
//		buffer.flip();
//		remoteEventMessage.iobject=buffer;
//		return thriftSerializerFactory.getActorMessageWithSubClass(Operation.EVENT_REMOTE_MESSAGE,remoteEventMessage);
//	}
}
