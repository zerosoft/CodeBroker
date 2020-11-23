package com.codebroker.demo.request;

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.item.ItemBean;

public class ItemBeanHandler extends AbstractClientRequestHandler<ItemBean> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, ItemBean request) {
		int  itemid = request.getItemId();
		int  itemcount = request.getItemCount();
	}
}