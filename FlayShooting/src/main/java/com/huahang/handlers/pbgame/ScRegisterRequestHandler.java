package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScRegisterBean;

public class  ScRegisterRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScRegisterBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScRegisterBean bean=new ScRegisterBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScRegisterBean bean=(ScRegisterBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScRegisterBean bean=(ScRegisterBean) params;
		return null;
	}

}
