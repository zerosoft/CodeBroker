package com.avic.sever.game.handler.login;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.WorldManager;
import com.codebroker.api.AppContext;
import com.codebroker.api.IArea;
import com.codebroker.api.IUser;

public class LoginRequest extends AbstractClientRequestHandler {

	public static final int REQUEST_ID = CommandID.LOGIN;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		try {
			userId = user.getUserId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject object = new JSONObject();
		object.put("state", "ok");
		object.put("name", name);
		object.put("userId", userId);

		IArea areaById = AppContext.getAreaManager().getAreaById(1);
		areaById.enterArea(user);
		return object.toJSONString();
	}
}
