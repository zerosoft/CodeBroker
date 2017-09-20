package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.SC_CHAT_PRV.Builder;

public class ScChatPrvBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.SC_CHAT_PRV_VALUE;
	
	private java.lang.String name;
	private java.lang.String message;


	public java.lang.String getName()
	{
		return name;
	}

	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	

	public java.lang.String getMessage()
	{
		return message;
	}

	public void setMessage(java.lang.String message)
	{
		this.message = message;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.SC_CHAT_PRV protocal = (com.message.protocol.PBGame.SC_CHAT_PRV) message;
			this.setName(protocal.getName());
			this.setMessage(protocal.getMessage());
	}

	@Override
	public com.message.protocol.PBGame.SC_CHAT_PRV javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.SC_CHAT_PRV.newBuilder();
			{
			newBuilder.setName(this.getName());
			}
			{
			newBuilder.setMessage(this.getMessage());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.SC_CHAT_PRV bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.SC_CHAT_PRV.parseFrom(bytes);
	}
}