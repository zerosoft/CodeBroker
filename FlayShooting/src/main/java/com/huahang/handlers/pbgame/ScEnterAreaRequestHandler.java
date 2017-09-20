package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScEnterAreaBean;

public class  ScEnterAreaRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScEnterAreaBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScEnterAreaBean bean=new ScEnterAreaBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScEnterAreaBean bean=(ScEnterAreaBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScEnterAreaBean bean=(ScEnterAreaBean) params;
		return null;
	}

}
