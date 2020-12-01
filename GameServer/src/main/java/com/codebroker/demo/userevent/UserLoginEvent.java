package com.codebroker.demo.userevent;

import com.codebroker.api.AppContext;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IGameUserEventListener;
import com.codebroker.api.internal.IResultStatusMessage;
import com.codebroker.demo.service.alliance.AllianceService;
import com.codebroker.demo.service.item.ItemService;
import com.codebroker.demo.service.user.UserService;
import com.codebroker.extensions.service.RequestKeyMessage;

import java.util.concurrent.CompletableFuture;

public class UserLoginEvent implements IGameUserEventListener<Object> {

	@Override
	public void handleEvent(IGameUser gameUser, Object event) {
		getGameUserEventListenerLogger().info("get Event game user id  {}",gameUser.getUserId());

		RequestKeyMessage requestKeyMessage=new RequestKeyMessage(2,gameUser.getUserId());

		AppContext.getManager(AllianceService.class).ifPresent(c->c.handleMessage(requestKeyMessage));
		AppContext.getManager(ItemService.class).ifPresent(c->c.handleMessage(requestKeyMessage));
		AppContext.getManager(UserService.class).ifPresent(c->c.handleMessage(requestKeyMessage));

		final  RequestKeyMessage requestKeyMessag1e=new RequestKeyMessage(1,gameUser.getUserId());
		AllianceService allianceService = AppContext.getManager(AllianceService.class).get();
		UserService userService = AppContext.getManager(UserService.class).get();
		ItemService itemService = AppContext.getManager(ItemService.class).get();
		CompletableFuture<IResultStatusMessage> join1 = CompletableFuture.completedFuture(allianceService.handleBackMessage(requestKeyMessag1e))
				.whenComplete((R, E) -> System.out.println(R.getMessage())).toCompletableFuture();
		CompletableFuture<IResultStatusMessage> join2 = CompletableFuture.completedFuture(userService.handleBackMessage(requestKeyMessag1e))
				.whenComplete((R, E) -> System.out.println(R.getMessage())).toCompletableFuture();
		CompletableFuture<IResultStatusMessage> join3 = CompletableFuture.completedFuture(itemService.handleBackMessage(requestKeyMessag1e))
				.whenComplete((R, E) -> System.out.println(R.getMessage())).toCompletableFuture();

		Void join = CompletableFuture.allOf(join1, join2, join3).whenComplete((v,t)->System.out.println("Result")).join();
		System.out.println("Result 1");
	}
}
