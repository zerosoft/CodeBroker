package com.huahang.handlers.pbgame;

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import com.huahang.message.bean.pbgame.CsGetGridListBean;
import com.message.protocol.PBGame.CS_GET_GRID_LIST.*;

public class  CsGetGridListRequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =CsGetGridListBean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		CsGetGridListBean bean=new CsGetGridListBean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		CsGetGridListBean bean=(CsGetGridListBean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		CsGetGridListBean bean=(CsGetGridListBean) params;
		return null;
	}

}
