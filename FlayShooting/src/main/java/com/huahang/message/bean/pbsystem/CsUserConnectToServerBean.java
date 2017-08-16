package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER.Builder;

public class CsUserConnectToServerBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.CS_USER_CONNECT_TO_SERVER_VALUE;
	
	private java.lang.String name;
	private java.lang.String params;


	public java.lang.String getName()
	{
		return name;
	}

	public void setName(java.lang.String name)
	{
		this.name = name;
	}
	

	public java.lang.String getParams()
	{
		return params;
	}

	public void setParams(java.lang.String params)
	{
		this.params = params;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER protocal = (com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER) message;
			this.setName(protocal.getName());
			this.setParams(protocal.getParams());
	}

	@Override
	public com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER.newBuilder();
			{
			newBuilder.setName(this.getName());
			}
			{
			newBuilder.setParams(this.getParams());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.CS_USER_CONNECT_TO_SERVER.parseFrom(bytes);
	}
}