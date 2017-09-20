package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScChatBean;

public class  ScChatRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScChatBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScChatBean bean=new ScChatBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScChatBean bean=(ScChatBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScChatBean bean=(ScChatBean) params;
		return null;
	}

}
