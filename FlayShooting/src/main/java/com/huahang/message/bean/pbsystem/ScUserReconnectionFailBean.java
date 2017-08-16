package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL.Builder;

public class ScUserReconnectionFailBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.SC_USER_RECONNECTION_FAIL_VALUE;
	
	private java.lang.String key;


	public java.lang.String getKey()
	{
		return key;
	}

	public void setKey(java.lang.String key)
	{
		this.key = key;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL protocal = (com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL) message;
			this.setKey(protocal.getKey());
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL.newBuilder();
			{
			newBuilder.setKey(this.getKey());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.SC_USER_RECONNECTION_FAIL.parseFrom(bytes);
	}
}