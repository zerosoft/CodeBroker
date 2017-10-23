package com.avic.sever.game.manager;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.avic.sever.game.entity.Room;
import com.avic.sever.game.handler.CommandID;
import com.codebroker.api.IUser;

public class RoomManager {
	private int roomId = 1;
	private List<Room> roomList = new ArrayList<Room>();

	public static RoomManager getInstance() {
		return inner.instance;
	}

	static class inner {
		static RoomManager instance = new RoomManager();
	}

	public Room createRoom(IUser user) {
		Room room = new Room();
		room.setId( roomId++);
		try {
			room.setOnwerId(user.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		room.getList().add(user);
		roomList.add(room);
		return room;
	}

	public Room joinRoom(IUser user, int roomId) {
		for (Room room : roomList) {
			if (room.getId() == roomId) {
				room.getList().add(user);
				return room;
			}
		}
		return null;
	}

	public Room getRoomById(int roomId) {
		for (Room room : roomList) {
			if (room.getId() == roomId) {
				return room;
			}
		}
		return null;
	}

	public void removeRoom(Integer roomid2) {
		for (Room room : roomList) {
			if (room.getId() == roomid2) {
				for (IUser user : room.getList()) {
					JSONObject result = new JSONObject();
					result.put("roomid", roomid2);
					user.sendMessageToIoSession(CommandID.REMOVE_ROOM, result.toString().getBytes());
				}
			}
		}
	}

	public void leave(Integer roomid2, IUser user) {
		for (Room room : roomList) {
			if (room.getId() == roomid2) {
				for (IUser users : room.getList()) {
					if (user.equals(users)) {
						room.getList().remove(user);
						JSONObject result = new JSONObject();
						result.put("state", true);
						user.sendMessageToIoSession(CommandID.LEAVE_ROOM, result.toString().getBytes());
						removeRoom(roomid2);
					} else {
						JSONObject result = new JSONObject();
						result.put("state", true);
						try {
							result.put("id", user.getUserId());
						} catch (Exception e) {
							e.printStackTrace();
						}
						user.sendMessageToIoSession(CommandID.LEAVE_ROOM, result.toString().getBytes());
					}
				}
			}
		}
	}
	
	public void sendRoomChat(Integer roomid, String message){
		for (Room room : roomList) {
			if (room.getId() == roomid) {
				for (IUser users : room.getList()) {
						JSONObject result = new JSONObject();
						result.put("state", true);
						result.put("mg", message);
						users.sendMessageToIoSession(CommandID.ROOM_CHAT_RECIVE, result.toString().getBytes());
				}
			}
		}
	
	}

	public List<Room> getAllRoom() {
		return roomList;
	}
}
