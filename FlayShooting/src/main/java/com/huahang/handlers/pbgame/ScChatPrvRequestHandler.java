package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScChatPrvBean;
import com.message.protocol.PBGame.SC_CHAT_PRV.*;

public class  ScChatPrvRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScChatPrvBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScChatPrvBean bean=new ScChatPrvBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScChatPrvBean bean=(ScChatPrvBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScChatPrvBean bean=(ScChatPrvBean) params;
		return null;
	}

}
