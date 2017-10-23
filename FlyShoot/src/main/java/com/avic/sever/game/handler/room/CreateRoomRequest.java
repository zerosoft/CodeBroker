package com.avic.sever.game.handler.room;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.codebroker.api.IUser;


public class CreateRoomRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.CREATE_ROOM;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		Room createRoom = RoomManager.getInstance().createRoom(user);
		user.getIObject().putInt("room_own", createRoom.getId());
		user.getIObject().putInt("room_in", createRoom.getId());
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("roomid", createRoom.getId());
		jsonObject.put("room_size", createRoom.getList().size());
		return jsonObject.toJSONString();
	}
}
