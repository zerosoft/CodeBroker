package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL.Builder;

public class ScUserConnectToServerFailBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.SC_USER_CONNECT_TO_SERVER_FAIL_VALUE;
	


	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL protocal = (com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL) message;
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL.newBuilder();
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.SC_USER_CONNECT_TO_SERVER_FAIL.parseFrom(bytes);
	}
}