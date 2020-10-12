//package com.codebroker.demo.request;
//
//import com.codebroker.api.AppContext;
//import com.codebroker.api.IClientRequestHandler;
//import com.codebroker.api.IGameUser;
//import com.codebroker.api.IGameWorld;
//import com.codebroker.api.event.Event;
//import com.codebroker.core.data.CObject;
//import com.codebroker.protobuf.Login_C;
//import com.codebroker.protobuff.login.LoginRequest;
//
//import java.util.Optional;
//
//public class LoginRequestHandler implements IClientRequestHandler<LoginRequest> {
//	@Override
//	public void handleClientRequest(IGameUser user, LoginRequest message) {
//		String account = message.getAccount();
//		String password = message.getPassword();
//
//		String userId = user.getUserId();
//		getClientRequestLogger().info("User Id {}",userId);
//		getClientRequestLogger().info("account {} password {}",account,password);
////		user.sendMessageToIoSession(101,"hello world".getBytes());
////
//////		Event event=new Event();
//////		event.setTopic("login");
//////		event.setMessage(CObject.newInstance());
//////		user.dispatchEvent(event);
////
////		IGameWorld gameWorld = AppContext.getGameWorld();
////		Optional<IGameUser> iGameUserById = gameWorld.findIGameUserById("3451");
////		if (iGameUserById.isPresent()){
////			getClientRequestLogger().info("get it {}",iGameUserById.get().getUserId());
////		}
////
////		CObject object = CObject.newInstance();
////		object.putUtfString("message","hello");
////		object.putClass("IGame",user);
////		AppContext.getGameWorld().sendMessageToService("ChatService", object);
//	}
//}
//
