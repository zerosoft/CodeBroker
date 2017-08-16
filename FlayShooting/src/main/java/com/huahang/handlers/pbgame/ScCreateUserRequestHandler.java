package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScCreateUserBean;
import com.message.protocol.PBGame.SC_CREATE_USER.*;

public class  ScCreateUserRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScCreateUserBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScCreateUserBean bean=new ScCreateUserBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScCreateUserBean bean=(ScCreateUserBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScCreateUserBean bean=(ScCreateUserBean) params;
		return null;
	}

}
