package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.SC_USER_ENTER_WORLD.Builder;

public class ScUserEnterWorldBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.SC_USER_ENTER_WORLD_VALUE;
	


	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.SC_USER_ENTER_WORLD protocal = (com.message.protocol.PBSystem.SC_USER_ENTER_WORLD) message;
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_ENTER_WORLD javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.SC_USER_ENTER_WORLD.newBuilder();
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_ENTER_WORLD bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.SC_USER_ENTER_WORLD.parseFrom(bytes);
	}
}