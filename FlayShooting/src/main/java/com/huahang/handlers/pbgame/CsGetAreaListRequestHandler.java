package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsGetAreaListBean;

public class  CsGetAreaListRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsGetAreaListBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsGetAreaListBean bean=new CsGetAreaListBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsGetAreaListBean bean=(CsGetAreaListBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsGetAreaListBean bean=(CsGetAreaListBean) params;
		return null;
	}

}
