package com.codebroker.demo.request;

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.item.ItemBean;

public class ItemBeanHandler implements IClientRequestHandler<ItemBean> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, ItemBean request) {
		int  itemid = request.getItemId();
		int  itemcount = request.getItemCount();
	}
}