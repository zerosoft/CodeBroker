package com.avic.sever.game;

import java.util.UUID;

import com.avic.sever.game.manager.RoomManager;
import com.avic.sever.game.manager.WorldManager;
import com.codebroker.api.IUser;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.request.AppListenerExtension;

public class GameEngine extends AppListenerExtension{

	@Override
	public String handleLogin(String name, String parms) throws NoAuthException {
		return UUID.randomUUID().toString();
	}

	@Override
	public boolean handleLogout(IUser user) {
		WorldManager.getInstance().leaveWorld(user);
		Integer roomin = user.getIObject().getInt("room_in");
		if (roomin!=null) {
			RoomManager.getInstance().leave(roomin, user);
		}
		return false;
	}

	@Override
	public boolean userReconnection(IUser user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(Object obj) {
		System.err.println("初始化");
		HandlerRegisterCenter.registerServerEventHandler(this);
		System.err.println("初始化 网络命令");
	}

}
