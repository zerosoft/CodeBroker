package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsCreateUserBean;
import com.message.protocol.PBGame.CS_CREATE_USER.*;

public class  CsCreateUserRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsCreateUserBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsCreateUserBean bean=new CsCreateUserBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsCreateUserBean bean=(CsCreateUserBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsCreateUserBean bean=(CsCreateUserBean) params;
		return null;
	}

}
