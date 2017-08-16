package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS.Builder;

public class ScUserReconnectionSuccessBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.SC_USER_RECONNECTION_SUCCESS_VALUE;
	


	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS protocal = (com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS) message;
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS.newBuilder();
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.SC_USER_RECONNECTION_SUCCESS.parseFrom(bytes);
	}
}