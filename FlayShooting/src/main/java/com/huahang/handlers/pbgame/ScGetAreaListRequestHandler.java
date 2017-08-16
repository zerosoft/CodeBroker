package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.ScGetAreaListBean;
import com.message.protocol.PBGame.SC_GET_AREA_LIST.*;

public class  ScGetAreaListRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =ScGetAreaListBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		ScGetAreaListBean bean=new ScGetAreaListBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		ScGetAreaListBean bean=(ScGetAreaListBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		ScGetAreaListBean bean=(ScGetAreaListBean) params;
		return null;
	}

}
