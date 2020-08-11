package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.core.data.CObject;
import com.codebroker.demo.request.DoSomeThingRequestHandler;
import com.codebroker.demo.request.UserDisconnectionRequestHandler;
import com.codebroker.demo.service.AllianceService;
import com.codebroker.demo.service.ChatService;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.google.common.collect.Maps;
import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.util.cl.ExtendedURLClassLoader;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.App;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoExtension extends AppListenerExtension {

	private Logger logger= LoggerFactory.getLogger(DemoExtension.class);

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

		AppContext.setManager(new AllianceService());

//		AppContext.setManager(new ChatService());

		AllianceService manager = AppContext.getManager(AllianceService.class);
		manager.init("hello world");

		boolean chatService = AppContext.getGameWorld().createGlobalService("ChatService", new ChatService());

		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CObject object = CObject.newInstance();
		object.putUtfString("message","hello");
		AppContext.getGameWorld().sendMessageToService("ChatService", object);

		Thread t=new Thread(()->{
			while (true){
				FindFile ff = new FindFile()
						.recursive(true)
						.includeDirs(true)
						.searchPath("E:\\github\\AvalonNew\\Demo1\\build\\libs");
				List<File> all = ff.findAll();
				URL[] classpath=new URL[all.size()];
				for (int i = 0; i < all.size(); i++) {
					try {
						classpath[i]=all.get(i).toURI().toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				ExtendedURLClassLoader extendedURLClassLoader=new ExtendedURLClassLoader(classpath,ClassLoader.getSystemClassLoader(),true);
				try {
					Class<?> aClass = extendedURLClassLoader.loadClass("com.game.server.main");
					Object o = aClass.newInstance();
					MethodUtils.invokeExactMethod(o,"sayHelloWorld");
					System.out.println(o);
					Thread.sleep(10000L);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		);
		t.start();
	}
}
