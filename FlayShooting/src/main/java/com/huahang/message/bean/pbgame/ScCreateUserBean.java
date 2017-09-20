package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.SC_CREATE_USER.Builder;

public class ScCreateUserBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.SC_CREATE_USER_VALUE;
	
	private java.lang.String states;


	public java.lang.String getStates()
	{
		return states;
	}

	public void setStates(java.lang.String states)
	{
		this.states = states;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.SC_CREATE_USER protocal = (com.message.protocol.PBGame.SC_CREATE_USER) message;
			this.setStates(protocal.getStates());
	}

	@Override
	public com.message.protocol.PBGame.SC_CREATE_USER javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.SC_CREATE_USER.newBuilder();
			{
			newBuilder.setStates(this.getStates());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.SC_CREATE_USER bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.SC_CREATE_USER.parseFrom(bytes);
	}
}