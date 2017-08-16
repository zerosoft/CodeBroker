package com.huahang.message.bean.pbgame;

import com.google.protobuf.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.codebroker.api.JavaProtocolTransform;
import com.message.protocol.PBGame.SC_LOGIN.*;

public class ScLoginBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.SC_LOGIN_VALUE;
	
	private int result;


	public int getResult()
	{
		return result;
	}

	public void setResult(int result)
	{
		this.result = result;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.SC_LOGIN protocal = (com.message.protocol.PBGame.SC_LOGIN) message;
			this.setResult(protocal.getResult());
	}

	@Override
	public com.message.protocol.PBGame.SC_LOGIN javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.SC_LOGIN.newBuilder();
			{
			newBuilder.setResult(this.getResult());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.SC_LOGIN bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.SC_LOGIN.parseFrom(bytes);
	}
}