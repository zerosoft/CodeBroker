package com.huahang;

import java.util.UUID;

import com.codebroker.api.AppContext;
import com.codebroker.api.IArea;
import com.codebroker.api.IUser;
import com.codebroker.database.JongoDBService;
import com.codebroker.extensions.request.AppListenerExtension;
import com.codebroker.util.PropertiesWrapper;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import distributed.ReplicatedCache;

public class GameServerListener extends AppListenerExtension {

	PropertiesWrapper configProperties;

	public PropertiesWrapper getConfigPropertieWrapper() {
		return configProperties;
	}

	@Override
	public void init(Object obj) {
		this.configProperties = (PropertiesWrapper) obj;
		/**
		 * 注册handler
		 */
		HandlerRegisterCenter.registerServerEventHandler(this);
		// 初始化DB
		JongoDBService dbService = new JongoDBService();
		dbService.init(obj);
		
		ActorSystem actorSystem = AppContext.getActorSystem();
		ActorRef actorOf = actorSystem.actorOf(Props.create(ReplicatedCache.class), "ReplicatedCache");
		System.out.println(actorOf.path().toString());
		try {
			IArea createGrid1 = AppContext.getAreaManager().createArea(1);
			createGrid1.createGrid("G1");
			createGrid1.createGrid("G2");
			createGrid1.createGrid("G3");
			IArea createGrid2 = AppContext.getAreaManager().createArea(2);
			createGrid2.createGrid("G1");
			createGrid2.createGrid("G2");
			createGrid2.createGrid("G3");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void destroy(Object obj) {
		super.destroy(obj);
	}

	/**
	 * 处理登入，并返回重连接使用的key
	 */
	@Override
	public String handleLogin(String name, String parms) {
		return UUID.randomUUID().toString();
	}

	@Override
	public boolean handleLogout(IUser user) {
		return false;
	}

	@Override
	public boolean userReconnection(IUser user) {
		return false;
	}

}
