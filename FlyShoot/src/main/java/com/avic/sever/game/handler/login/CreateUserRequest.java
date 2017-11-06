package com.avic.sever.game.handler.login;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.model.AccountEntity;
import com.avic.sever.game.model.AccountManager;
import com.avic.sever.game.model.UserEntity;
import com.avic.sever.game.model.UserManager;
import com.codebroker.api.AppContext;
import com.codebroker.api.IArea;
import com.codebroker.api.IUser;
import com.codebroker.core.manager.JongoDBService;
import com.codebroker.setting.PrefixConstant;

public class CreateUserRequest extends AbstractClientRequestHandler {

	public static final int REQUEST_ID = CommandID.CREATE_USER;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		JSONObject jsonObject=JSONObject.parseObject(jsonString);

		String img = jsonObject.getString("img");
		String name = jsonObject.getString("name");
		UserEntity userEntity=new UserEntity();
		userEntity.setUserId(user.getIObject().getUtfString(PrefixConstant.LOGIN_NAME));
		userEntity.setAccountId(user.getIObject().getUtfString(PrefixConstant.LOGIN_NAME));
		userEntity.setLevel(1);
		userEntity.setUserName(name);
		userEntity.setHeadImg(img);

		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		 UserManager.getInstance().createUser(manager.getJongo(), userEntity);

		String loginName = user.getIObject().getUtfString(PrefixConstant.LOGIN_NAME);
		String loginParms = user.getIObject().getUtfString(PrefixConstant.LOGIN_PARMS);

		AccountEntity accountEntity = AccountManager.getInstance().selectAccount(manager.getJongo(), loginName, loginParms);
		accountEntity.setUserId(userEntity.getUserId());
		AccountManager.getInstance().updateAccount(manager.getJongo(),accountEntity);
		
		JSONObject object = new JSONObject();
		object.put("state", "ok");
		object.put("name", name);
		object.put("userId", img);

		return object.toJSONString();
	}
}
