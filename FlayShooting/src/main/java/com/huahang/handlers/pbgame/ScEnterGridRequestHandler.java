package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScEnterGridBean;
import com.message.protocol.PBGame.SC_ENTER_GRID.*;

public class  ScEnterGridRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScEnterGridBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScEnterGridBean bean=new ScEnterGridBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScEnterGridBean bean=(ScEnterGridBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScEnterGridBean bean=(ScEnterGridBean) params;
		return null;
	}

}
