package com.avic.sever.game.handler.world;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.avic.sever.game.manager.WorldManager;
import com.codebroker.api.IUser;

import jnr.ffi.Struct.nlink_t;


public class SendMessageWorldRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.WORD_CHAT_SEND;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		JSONObject jsonObject=JSONObject.parseObject(jsonString);
		String message=jsonObject.getString("msg");
		WorldManager.getInstance().sendWorldMessage(userId, message);
		return null;
	}
}
