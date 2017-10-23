package com.avic.sever.game.handler.room;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.codebroker.api.IUser;


public class JoinRoomRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.JOIN_ROOM;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		JSONObject jsonObject=JSONObject.parseObject(jsonString);
		Integer int1 = user.getIObject().getInt("room_in");
		
		Integer roomid = jsonObject.getInteger("roomid");
		if (int1==roomid) {
			JSONObject result=new JSONObject();
			jsonObject.put("state", false);
			return result.toJSONString();
		}
		Room joinRoom = RoomManager.getInstance().joinRoom(user, roomid);
		user.getIObject().putInt("room_in", joinRoom.getId());
		JSONObject result=new JSONObject();
		result.put("state", true);
		result.put("roomid", joinRoom.getId());
		result.put("room_size", joinRoom.getList().size());
		return result.toJSONString();
	}
}
