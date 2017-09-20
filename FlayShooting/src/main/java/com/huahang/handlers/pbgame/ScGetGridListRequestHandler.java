package com.huahang.handlers.pbgame;

import com.codebroker.api.IUser;
import com.codebroker.api.JavaProtocolTransform;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScGetGridListBean;

public class  ScGetGridListRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScGetGridListBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScGetGridListBean bean=new ScGetGridListBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScGetGridListBean bean=(ScGetGridListBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScGetGridListBean bean=(ScGetGridListBean) params;
		return null;
	}

}
