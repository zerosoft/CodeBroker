package com.avic.sever.game;

import java.util.List;
import java.util.UUID;

import akka.actor.ActorRef;
import com.avic.sever.game.manager.RoomManager;
import com.avic.sever.game.manager.WorldManager;
import com.avic.sever.game.model.AccountEntity;
import com.avic.sever.game.model.AccountManager;
import com.codebroker.api.AppContext;
import com.codebroker.api.IUser;
import com.codebroker.api.manager.IAreaManager;
import com.codebroker.core.manager.JongoDBService;
import com.codebroker.exception.AllReadyRegeditException;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.request.AppListenerExtension;
import com.codebroker.util.FileUtil;

public class GameEngine extends AppListenerExtension{

	@Override
	public String handleLogin(String name, String parms) throws NoAuthException {
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		AccountEntity accountEntity = AccountManager.getInstance().selectAccount(manager.getJongo(), name, parms);
		if (accountEntity!=null){
			return UUID.randomUUID().toString();
		}else{
			throw new NoAuthException();
		}
	}

	@Override
	public boolean handleRegedit(String name, String parms) throws AllReadyRegeditException {
		JongoDBService manager = AppContext.getManager(JongoDBService.class);
		boolean b = AccountManager.getInstance().checkRegedit(manager.getJongo(), name);
		if (b){
			throw new AllReadyRegeditException();
		}else {
			AccountEntity accountEntity = AccountManager.getInstance().regeditAccount(manager.getJongo(), name, parms);
			if (accountEntity==null){
				return false;
			}
			return true;
		}

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
		FileUtil.printOsEnv();
		System.err.println("初始化");
		HandlerRegisterCenter.registerServerEventHandler(this);
		System.err.println("初始化 网络命令");
		JongoDBService jongoDBService=new JongoDBService();
		jongoDBService.init(obj);
		try {
			IAreaManager areaManager=AppContext.getAreaManager();
			areaManager.createArea(1);
			areaManager.createArea(2);
			areaManager.createArea(3);

			areaManager.removeArea(2);

			List<ActorRef> allArea = areaManager.getAllArea();
			for (ActorRef act :
					allArea) {
				System.err.println(act.path());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
