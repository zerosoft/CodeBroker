package com.huahang.message.bean.pbsystem;

import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.message.protocol.PBSystem.CS_USER_DISCONNECT.Builder;

public class CsUserDisconnectBean implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = com.message.protocol.Message.PB.SystemKey.CS_USER_DISCONNECT_VALUE;
	
	private java.lang.String stat;


	public java.lang.String getStat()
	{
		return stat;
	}

	public void setStat(java.lang.String stat)
	{
		this.stat = stat;
	}
	

	@Override
	public void protocolToJavaBean(Message message)
	{
		com.message.protocol.PBSystem.CS_USER_DISCONNECT protocal = (com.message.protocol.PBSystem.CS_USER_DISCONNECT) message;
			this.setStat(protocal.getStat());
	}

	@Override
	public com.message.protocol.PBSystem.CS_USER_DISCONNECT javaBeanToProtocol()
	{
		Builder newBuilder = com.message.protocol.PBSystem.CS_USER_DISCONNECT.newBuilder();
			{
			newBuilder.setStat(this.getStat());
			}
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public com.message.protocol.PBSystem.CS_USER_DISCONNECT bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return com.message.protocol.PBSystem.CS_USER_DISCONNECT.parseFrom(bytes);
	}
}