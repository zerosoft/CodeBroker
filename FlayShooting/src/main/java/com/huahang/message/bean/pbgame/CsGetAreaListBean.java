package com.huahang.message.bean.pbgame;

import com.google.protobuf.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.codebroker.api.JavaProtocolTransform;
import com.message.protocol.PBGame.CS_GET_AREA_LIST.*;

public class CsGetAreaListBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_GET_AREA_LIST_VALUE;
	
	private java.lang.String worldId;


	public java.lang.String getWorldId()
	{
		return worldId;
	}

	public void setWorldId(java.lang.String worldId)
	{
		this.worldId = worldId;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.CS_GET_AREA_LIST protocal = (com.message.protocol.PBGame.CS_GET_AREA_LIST) message;
			this.setWorldId(protocal.getWorldId());
	}

	@Override
	public com.message.protocol.PBGame.CS_GET_AREA_LIST javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_GET_AREA_LIST.newBuilder();
			{
			newBuilder.setWorldId(this.getWorldId());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.CS_GET_AREA_LIST bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_GET_AREA_LIST.parseFrom(bytes);
	}
}