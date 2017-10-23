package com.avic.sever.game.handler.room;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.codebroker.api.IUser;

import jnr.ffi.Struct.nlink_t;


public class SendMessageRoomRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.ROOM_CHAT_SEND;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		JSONObject jsonObject=JSONObject.parseObject(jsonString);
		String message=jsonObject.getString("msg");
		int roomid = user.getIObject().getInt("room_in");
		RoomManager.getInstance().sendRoomChat(roomid, message);
		return null;
		
	}
}
