package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsEnterGridBean;
import com.message.protocol.PBGame.CS_ENTER_GRID.*;

public class  CsEnterGridRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsEnterGridBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsEnterGridBean bean=new CsEnterGridBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsEnterGridBean bean=(CsEnterGridBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsEnterGridBean bean=(CsEnterGridBean) params;
		return null;
	}

}
