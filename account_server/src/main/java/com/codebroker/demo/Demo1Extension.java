package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.IGameWorld;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.data.CObject;
import com.codebroker.demo.request.DoSomeThingRequestHandler;
import com.codebroker.demo.request.UserDisconnectionRequestHandler;
import com.codebroker.demo.service.AllianceService;
import com.codebroker.demo.service.ChatService;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.demo.userevent.LoginBackSameEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Demo1Extension extends AppListenerExtension {

	private Logger logger= LoggerFactory.getLogger(Demo1Extension.class);

	private Map<String,String> userMap=new HashMap<>();
	//在线用户列表
	private Map<String,IGameUser> onlineUsers= Maps.newConcurrentMap();

	@Override
	public String sessionLoginVerification(String name, String parameter) throws NoAuthException {
		logger.info("handle login name {} parameter {}",name,parameter);
		return name;
	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}",user.getUserId());
		user.addEventListener("login", new DoSameEvent());
		user.addEventListener("login Chat back", new LoginBackSameEvent());
	}


	@Override
	public boolean handleLogout(IGameUser user) {
		logger.info("User LogOut parameter {}",user.getUserId());
		return false;
	}

	@Override
	public boolean userReconnection(IGameUser user) {
		return false;
	}

	@Override
	public void init(Object obj) {
		logger.info("Init");
		addRequestHandler(100, DoSomeThingRequestHandler.class);
		addRequestHandler(101, UserDisconnectionRequestHandler.class);

		IGameWorld gameWorld = AppContext.getGameWorld();
		boolean setManager = gameWorld.createGlobalService("AllianceService",new AllianceService());

//		AppContext.getGameWorld().getClusterService("AllianceService",new AllianceService());

//		System.out.println(setManager);
//		AppContext.setManager(new ChatService());

//		AllianceService manager = AppContext.getManager(AllianceService.class);
//		manager.init("hello world");

		boolean chatService = gameWorld.createGlobalService("ChatService", new ChatService());

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					Class<?> aClass = null;
					try {
						aClass =Demo1Extension.class.getClassLoader().loadClass("com.codebroker.demo.HelloWOrld");
//						aClass = ServerEngine.getiClassLoader().loadClass("com.codebroker.demo.HelloWOrld");
						MethodUtils.invokeExactMethod(aClass.newInstance(),"vs");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException exception) {
						exception.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();

		CObject object = CObject.newInstance();
		object.putUtfString("message", "hello");
		gameWorld.sendMessageToService("AllianceService", object);
	}

	@Override
	public void destroy(Object obj) {
		logger.info("Server Destroy");
		clearAllHandlers();
	}
}
