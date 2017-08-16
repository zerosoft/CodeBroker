package com.huahang.message.bean.pbgame;

import com.google.protobuf.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.codebroker.api.JavaProtocolTransform;
import com.message.protocol.PBGame.CS_ENTER_AREA.*;

public class CsEnterAreaBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_ENTER_AREA_VALUE;
	
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
		com.message.protocol.PBGame.CS_ENTER_AREA protocal = (com.message.protocol.PBGame.CS_ENTER_AREA) message;
			this.setMessage(protocal.getMessage());
	}

	@Override
	public com.message.protocol.PBGame.CS_ENTER_AREA javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_ENTER_AREA.newBuilder();
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
	public com.message.protocol.PBGame.CS_ENTER_AREA bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_ENTER_AREA.parseFrom(bytes);
	}
}