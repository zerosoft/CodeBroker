package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsLoginBean;
import com.message.protocol.PBGame.CS_LOGIN.*;

public class  CsLoginRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsLoginBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsLoginBean bean=new CsLoginBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsLoginBean bean=(CsLoginBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsLoginBean bean=(CsLoginBean) params;
		return null;
	}

}
