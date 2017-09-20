package com.huahang.message.bean.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBGame.SC_GET_AREA_LIST.Builder;

public class ScGetAreaListBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.MessageKey.SC_GET_AREA_LIST_VALUE;
	
	private java.util.List<java.lang.String> areaId;


	public java.util.List<java.lang.String> getAreaId()
	{
		return areaId;
	}

	public void setAreaId(java.util.List<java.lang.String> areaId)
	{
		this.areaId = areaId;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBGame.SC_GET_AREA_LIST protocal = (com.message.protocol.PBGame.SC_GET_AREA_LIST) message;
				{
				java.util.List<java.lang.String> list = protocal.getAreaIdList();
				this.setAreaId(list);
				}
	}

	@Override
	public com.message.protocol.PBGame.SC_GET_AREA_LIST javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBGame.SC_GET_AREA_LIST.newBuilder();
						{
			newBuilder.addAllAreaId(this.getAreaId());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBGame.SC_GET_AREA_LIST bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBGame.SC_GET_AREA_LIST.parseFrom(bytes);
	}
}