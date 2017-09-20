package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsEnterAreaBean;

public class  CsEnterAreaRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsEnterAreaBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsEnterAreaBean bean=new CsEnterAreaBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsEnterAreaBean bean=(CsEnterAreaBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsEnterAreaBean bean=(CsEnterAreaBean) params;
		return null;
	}

}
