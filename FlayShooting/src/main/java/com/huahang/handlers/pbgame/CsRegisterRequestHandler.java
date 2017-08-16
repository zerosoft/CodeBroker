package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsRegisterBean;
import com.message.protocol.PBGame.CS_REGISTER.*;

public class  CsRegisterRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsRegisterBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsRegisterBean bean=new CsRegisterBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsRegisterBean bean=(CsRegisterBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsRegisterBean bean=(CsRegisterBean) params;
		return null;
	}

}
