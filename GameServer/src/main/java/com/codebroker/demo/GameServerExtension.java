package com.codebroker.demo;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.component.service.DataSourceComponent;
import com.codebroker.component.service.MybatisComponent;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.demo.request.CreateRequestHandler;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.demo.service.item.ItemService;
import com.codebroker.demo.userevent.DoSameEvent;
import com.codebroker.demo.userevent.UserRemoveEvent;
import com.codebroker.exception.NoAuthException;
import com.codebroker.extensions.AppListenerExtension;
import com.codebroker.extensions.service.RequestKeyMessage;
import com.codebroker.mybatis.gameserver1.mapper.GameUserMapper;
import com.codebroker.mybatis.gameserver1.model.GameUser;
import com.codebroker.mybatis.gameserver1.model.GameUserExample;
import com.google.common.collect.Maps;
import jodd.io.FileUtil;
import jodd.io.findfile.ClassScanner;
import jodd.util.ClassLoaderUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URL;
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


		RequestKeyMessage requestKeyMessage=new RequestKeyMessage<Integer,JsonObject>(1,jsonObject);

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
		user.addEventListener(IGameUser.UserEvent.LOGIN, new DoSameEvent());
		user.addEventListener(IGameUser.UserEvent.LOGOUT, new UserRemoveEvent());
		onlineUsers.put(user.getUserId(),user);
	}


	@Override
	public boolean handleLogout(IGameUser user) {
		logger.info("User LogOut parameter {}", user.getUserId());
		onlineUsers.remove(user.getUserId());
		return false;
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

		/**
		 * 初始化MyBatis组件
		 */
		MybatisComponent mybatisComponent=new MybatisComponent();
		mybatisComponent.init(obj);
		AppContext.setComponent(mybatisComponent);
		//创建服务
		AppContext.setManager(new AllianceService());
		AppContext.setManager(new ItemService());
		//初始化服务
		AppContext.getManager(AllianceService.class).ifPresent(c->c.init(null));
		AppContext.getManager(ItemService.class).ifPresent(c->c.init(null));
	}

}
