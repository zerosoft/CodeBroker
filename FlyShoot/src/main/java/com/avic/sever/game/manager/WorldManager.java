package com.avic.sever.game.manager;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.CommandID;
import com.codebroker.api.IUser;

public class WorldManager {
	private List<IUser> userList = new ArrayList<IUser>();

	public static WorldManager getInstance() {
		return inner.instance;
	}

	static class inner {
		static WorldManager instance = new WorldManager();
	}

	public void enterWorld(IUser iUser){
		userList.add(iUser);
	}
	
	public void leaveWorld(IUser iUser){
		userList.remove(iUser);
	}
	
	public void sendWorldMessage(String userId,String message){
		for (IUser users : userList) {
			JSONObject result = new JSONObject();
			result.put("userid", userId);
			result.put("message", message);
			users.sendMessageToIoSession(CommandID.WORD_CHAT_RECIVE, result.toString().getBytes());
	}
	}
}
