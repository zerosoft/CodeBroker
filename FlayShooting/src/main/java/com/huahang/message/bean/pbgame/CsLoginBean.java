package com.huahang.message.bean.pbgame;

import com.google.protobuf.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.codebroker.api.JavaProtocolTransform;
import com.message.protocol.PBGame.CS_LOGIN.*;

public class CsLoginBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_LOGIN_VALUE;
	
	private java.lang.String name;
	private java.lang.String password;


	public java.lang.String getName()
	{
		return name;
	}

	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	

	public java.lang.String getPassword()
	{
		return password;
	}

	public void setPassword(java.lang.String password)
	{
		this.password = password;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.CS_LOGIN protocal = (com.message.protocol.PBGame.CS_LOGIN) message;
			this.setName(protocal.getName());
			this.setPassword(protocal.getPassword());
	}

	@Override
	public com.message.protocol.PBGame.CS_LOGIN javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_LOGIN.newBuilder();
			{
			newBuilder.setName(this.getName());
			}
			{
			newBuilder.setPassword(this.getPassword());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.CS_LOGIN bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_LOGIN.parseFrom(bytes);
	}
}