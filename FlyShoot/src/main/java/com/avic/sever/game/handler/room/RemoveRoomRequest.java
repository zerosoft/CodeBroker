package com.avic.sever.game.handler.room;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.AbstractClientRequestHandler;
import com.avic.sever.game.handler.CommandID;
import com.avic.sever.game.manager.RoomManager;
import com.codebroker.api.IUser;

import jnr.ffi.Struct.nlink_t;


public class RemoveRoomRequest extends AbstractClientRequestHandler{
	
	public static final int REQUEST_ID =CommandID.REMOVE_ROOM;

	@Override
	public String handleRequest(IUser user, String jsonString) {
		JSONObject jsonObject=JSONObject.parseObject(jsonString);
		Integer roomid = jsonObject.getInteger("roomid");
		Integer room_in = user.getIObject().getInt("room_in");
		if (roomid==room_in) {
			Room roomById = RoomManager.getInstance().getRoomById(roomid);
			if (roomById==null) {
				user.getIObject().removeElement("room_in");
				user.getIObject().removeElement("room_own");
				JSONObject result=new JSONObject();
				jsonObject.put("state", false);
				return result.toJSONString();
			}else{
				if (roomById.getOnwerId().equals(name)) {
					RoomManager.getInstance().removeRoom(roomid);
					JSONObject result=new JSONObject();
					jsonObject.put("state", true);
					return result.toJSONString(); 
				}else{
					RoomManager.getInstance().leave(roomid,user);
					JSONObject result=new JSONObject();
					jsonObject.put("state", true);
					return result.toJSONString(); 
				}
			}
		}else{
			JSONObject result=new JSONObject();
			jsonObject.put("state", false);
			return result.toJSONString();
		}
		
	}
}
