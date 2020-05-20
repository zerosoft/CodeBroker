package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.api.IGameUser;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldMessage;
import com.google.common.collect.Maps;

import java.util.Map;

import static javafx.scene.input.KeyCode.T;

public class GameWorld extends AbstractBehavior<IGameWorldMessage> {

	public static final String IDENTIFY = GameWorld.class.getSimpleName();

	private long gameWorldId;
	Map<String, IGameUser> userMap= Maps.newTreeMap();

	public static Behavior<IGameWorldMessage> create(long id) {
		return Behaviors.setup(
				context -> {
					context
							.getSystem()
							.receptionist()
							.tell(Receptionist.register(ServiceKey.create(IGameWorldMessage.class, GameWorld.IDENTIFY+"."+id), context.getSelf()));
					return new GameWorld(context,id);
				});
	}


	public GameWorld(ActorContext<IGameWorldMessage> context, long id) {
		super(context);
		this.gameWorldId=id;
	}

	@Override
	public Receive<IGameWorldMessage> createReceive() {
		return newReceiveBuilder()
				.onMessage(IGameWorldMessage.findIGameUserByIdMessage.class,this::findGameUserById)
				.onMessage(IGameWorldMessage.UserLoginWorld.class,this::userLoginWorld)
				.onMessage(IGameWorldMessage.UserLogOutWorld.class,this::logoutWorld)
				.build();
	}

	private  Behavior<IGameWorldMessage> logoutWorld(IGameWorldMessage.UserLogOutWorld message) {

		userMap.remove(message.gameUser.getUserId());

		CodeBrokerAppListener appListener = ContextResolver.getAppListener();
		appListener.handleLogout(message.gameUser);

		return Behaviors.same();
	}

	private Behavior<IGameWorldMessage> userLoginWorld(IGameWorldMessage.UserLoginWorld message) {
		userMap.put(message.gameUser.getUserId(),message.gameUser);

		CodeBrokerAppListener appListener = ContextResolver.getAppListener();
		appListener.userLogin(message.gameUser);

		return Behaviors.same();
	}

	private Behavior<IGameWorldMessage> findGameUserById(IGameWorldMessage.findIGameUserByIdMessage message) {
		if (userMap.containsKey(message.id)){
			message.reply.tell(new IGameWorldMessage.FindGameUser(userMap.get(message.id)));
		}else {
			message.reply.tell(IGameWorldMessage.NoFindGameUser.INSTANCE);
		}
		return Behaviors.same();
	}
}
