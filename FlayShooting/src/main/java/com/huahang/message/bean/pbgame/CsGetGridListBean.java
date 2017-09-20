package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.CS_GET_GRID_LIST.Builder;

public class CsGetGridListBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.CS_GET_GRID_LIST_VALUE;
	
	private java.lang.String areaId;


	public java.lang.String getAreaId()
	{
		return areaId;
	}

	public void setAreaId(java.lang.String areaId)
	{
		this.areaId = areaId;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.CS_GET_GRID_LIST protocal = (com.message.protocol.PBGame.CS_GET_GRID_LIST) message;
			this.setAreaId(protocal.getAreaId());
	}

	@Override
	public com.message.protocol.PBGame.CS_GET_GRID_LIST javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.CS_GET_GRID_LIST.newBuilder();
			{
			newBuilder.setAreaId(this.getAreaId());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.CS_GET_GRID_LIST bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.CS_GET_GRID_LIST.parseFrom(bytes);
	}
}