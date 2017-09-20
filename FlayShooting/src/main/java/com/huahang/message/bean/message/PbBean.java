package com.huahang.message.bean.message;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.Message.PB.Builder;

public class PbBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.PB_VALUE;
	


	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.Message.PB protocal = (com.message.protocol.Message.PB) message;
	}

	@Override
	public com.message.protocol.Message.PB javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.Message.PB.newBuilder();
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.Message.PB bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.Message.PB.parseFrom(bytes);
	}
}