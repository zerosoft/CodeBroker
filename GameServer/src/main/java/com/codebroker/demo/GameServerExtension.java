package com.codebroker.demo;

import ch.qos.logback.core.joran.spi.JoranException;
import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.component.service.MybatisComponent;
import com.codebroker.demo.request.BuyItemRequestHandler;
import com.codebroker.demo.request.BuyItemResponseHandler;
import com.codebroker.demo.request.CreateAllianceRequestHandler;
import com.codebroker.demo.request.CreateRequestHandler;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.demo.service.item.ItemService;
import com.codebroker.demo.service.user.UserService;
import com.codebroker.demo.userevent.UserLoginEvent;
import com.codebroker.demo.userevent.UserLogoutEvent;
import com.codebroker.demo.userevent.UserLostSessionEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.mybatis.gameserver1.mapper.GameUserMapper;
import com.codebroker.mybatis.gameserver1.model.GameUser;
import com.codebroker.mybatis.gameserver1.model.GameUserExample;
import com.google.common.collect.Maps;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

public class GameServerExtension extends AppListenerExtension {

	private Logger logger = LoggerFactory.getLogger(GameServerExtension.class);

	//在线用户列表
	private Map<String, IGameUser> onlineUsers = Maps.newConcurrentMap();

	Gson gson=new Gson();

	@Override
	public String sessionLoginVerification(byte[] sourceProtocol) throws NoAuthException {
		JsonObject jsonElement = gson.fromJson(new String(sourceProtocol), JsonObject.class);
		String name=jsonElement.get("name").getAsString();
		String parameter=jsonElement.get("parm").getAsString();
		logger.info("handle login name {} parameter {}", name, parameter);

		JsonObject jsonObject=new JsonObject();
		//账号
		jsonObject.addProperty("name",name);
		//密码
		jsonObject.addProperty("password",parameter);


		RequestKeyMessage<Integer,JsonObject> requestKeyMessage=new RequestKeyMessage(1,jsonObject);

		IResultStatusMessage resultStatusMessage = AppContext.getGameWorld()
				.sendMessageToClusterIService("com.codebroker.demo.service.account.AccountService", requestKeyMessage);
		String uid;
		if (resultStatusMessage.getStatus().equals(IResultStatusMessage.Status.OK)) {
			JsonObject object = (JsonObject) resultStatusMessage.getMessage();
			uid = object.get("uid").getAsString();
			logger.info("user login {}",uid);
			return uid;
		} else {
			throw new NoAuthException();
		}

	}

	@Override
	public void userLogin(IGameUser user) {
		logger.info("User Login parameter {}", user.getUserId());
		Optional<MybatisComponent> optionalMybatisComponent=AppContext.getComponent(MybatisComponent.class);
		if (optionalMybatisComponent.isPresent()){
			Optional<SqlSessionFactory> game = optionalMybatisComponent.get().getSqlSessionFactory("game");
			boolean present = game.isPresent();
			if (present){
				SqlSessionFactory sqlSessionFactory = game.get();
				try (SqlSession session = sqlSessionFactory.openSession()) {
					GameUserMapper mapper = session.getMapper(GameUserMapper.class);
					GameUserExample gameUserExample=new GameUserExample();
					gameUserExample.createCriteria().andAccountUidEqualTo(user.getUserId());
					List<GameUser> gameUsers = mapper.selectByExample(gameUserExample);
					try {
						if (gameUsers.size()<1){
							GameUser gameUser=new GameUser();
							gameUser.setAccountUid(user.getUserId());
							gameUser.setUid(System.currentTimeMillis());
							gameUser.setName("");
							mapper.insertSelective(gameUser);
							session.commit();
						}
					}catch (Exception e){
						e.printStackTrace();
					}

				}
			}
		}
		user.addEventListener(IGameUser.UserEvent.LOGIN, new UserLoginEvent());
		user.addEventListener(IGameUser.UserEvent.LOGOUT, new UserLogoutEvent());
		user.addEventListener(IGameUser.UserEvent.LOST_CONNECTION, new UserLostSessionEvent());
		onlineUsers.put(user.getUserId(),user);

//		user.sendEventToSelf(new Event(IGameUser.UserEvent.LOGIN.name(),""));

	}


	@Override
	public boolean handleLogout(IGameUser user) {
		logger.info("User LogOut parameter {}", user.getUserId());
		onlineUsers.remove(user.getUserId());

		RequestKeyMessage requestKeyMessage=new RequestKeyMessage(3,user.getUserId());

		AppContext.getManager(AllianceService.class).ifPresent(c->c.handleMessage(requestKeyMessage));
		AppContext.getManager(ItemService.class).ifPresent(c->c.handleMessage(requestKeyMessage));
		AppContext.getManager(UserService.class).ifPresent(c->c.handleMessage(requestKeyMessage));
		return true;
	}

	@Override
	public boolean userReconnection(IGameUser user) {
		return false;
	}

	@Override
	public void init(Object obj) {

		logger.info("Init");
		//注册消息命令
		addRequestHandler(11, CreateRequestHandler.class);
		//模拟业务消耗，用户独立业务
		addRequestHandler(12, BuyItemRequestHandler.class);
		addRequestHandler(13, CreateAllianceRequestHandler.class);


		/**
		 * 初始化MyBatis组件
		 */
		MybatisComponent mybatisComponent=new MybatisComponent();
		mybatisComponent.init(obj);
		AppContext.setComponent(mybatisComponent);
		//创建服务
		AppContext.setManager(new AllianceService());
		AppContext.setManager(new ItemService());
		AppContext.setManager(new UserService());
		//初始化服务
		AppContext.getManager(AllianceService.class).ifPresent(c->c.init(null));
		AppContext.getManager(ItemService.class).ifPresent(c->c.init(null));
		AppContext.getManager(UserService.class).ifPresent(c->c.init(null));
	}

}
