package com.avic.sever.game.handler.login;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.WorldManager;
import com.avic.sever.game.model.AccountEntity;
import com.avic.sever.game.model.AccountManager;
import com.codebroker.api.AppContext;
import com.codebroker.api.IArea;
import com.codebroker.api.IUser;
import com.codebroker.core.manager.JongoDBService;
import com.codebroker.setting.PrefixConstant;

public class LoginRequest extends AbstractClientRequestHandler {

	public static final int REQUEST_ID = CommandID.LOGIN;

	@Override
	public String handleRequest(IUser user, String jsonString) {

		userId = user.getUserId();
		String loginName = user.getIObject().getUtfString(PrefixConstant.LOGIN_NAME);
		String loginParms = user.getIObject().getUtfString(PrefixConstant.LOGIN_PARMS);
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		AccountEntity accountEntity = AccountManager.getInstance().selectAccount(manager.getJongo(), loginName, loginParms);
		if (accountEntity==null){
			JSONObject object = new JSONObject();
			object.put("state", "no account");
			object.put("name", userId);
			object.put("userId", userId);
			return object.toJSONString();
		}else {
			if (accountEntity.getUserId()==null||accountEntity.getUserId().trim().equals("")){
				JSONObject object = new JSONObject();
				object.put("state", "create");
				object.put("name", userId);
				object.put("userId", userId);
				return object.toJSONString();
			}else{

			}
		}
		JSONObject object = new JSONObject();
		object.put("state", "ok");
		object.put("name", userId);
		object.put("userId", userId);

		IArea areaById = AppContext.getAreaManager().getAreaById(1);
		areaById.enterArea(user);
		return object.toJSONString();
	}
}
