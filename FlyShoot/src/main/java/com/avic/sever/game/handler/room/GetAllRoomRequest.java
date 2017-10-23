package com.avic.sever.game.handler.room;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.codebroker.api.IUser;


public class GetAllRoomRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.GET_ALL_ROOM;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		List<Room> allRoom = RoomManager.getInstance().getAllRoom();
		JSONObject jsonObject=new JSONObject();
		JSONArray array=new JSONArray();
		for (Room object : allRoom) {
			JSONObject object2=new JSONObject();
			object2.put("id", object.getId());
			object2.put("size", object.getList().size());
			array.add(object2);
		}
		jsonObject.put("state", true);
		jsonObject.put("list", array);
		String jsonString2 = jsonObject.toString();
		System.out.println(jsonString2);
		return jsonString2;
	}
}
