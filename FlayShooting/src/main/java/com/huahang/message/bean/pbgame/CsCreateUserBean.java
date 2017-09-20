package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.CS_CREATE_USER.Builder;

public class CsCreateUserBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_CREATE_USER_VALUE;
	
	private java.lang.String name;


	public java.lang.String getName()
	{
		return name;
	}

	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.CS_CREATE_USER protocal = (com.message.protocol.PBGame.CS_CREATE_USER) message;
			this.setName(protocal.getName());
	}

	@Override
	public com.message.protocol.PBGame.CS_CREATE_USER javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_CREATE_USER.newBuilder();
			{
			newBuilder.setName(this.getName());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.CS_CREATE_USER bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_CREATE_USER.parseFrom(bytes);
	}
}