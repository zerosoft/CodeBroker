package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.CS_CHAT.Builder;

public class CsChatBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_CHAT_VALUE;
	
	private java.lang.String message;


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
		com.message.protocol.PBGame.CS_CHAT protocal = (com.message.protocol.PBGame.CS_CHAT) message;
			this.setMessage(protocal.getMessage());
	}

	@Override
	public com.message.protocol.PBGame.CS_CHAT javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_CHAT.newBuilder();
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
	public com.message.protocol.PBGame.CS_CHAT bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_CHAT.parseFrom(bytes);
	}
}