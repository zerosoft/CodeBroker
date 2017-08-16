package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsChatPrvBean;
import com.message.protocol.PBGame.CS_CHAT_PRV.*;

public class  CsChatPrvRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsChatPrvBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsChatPrvBean bean=new CsChatPrvBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsChatPrvBean bean=(CsChatPrvBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsChatPrvBean bean=(CsChatPrvBean) params;
		return null;
	}

}
