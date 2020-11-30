package com.codebroker.core.actortype;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppListener;
import com.codebroker.api.IGameUser;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameWorldActor;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.core.entities.GameUser;
import com.codebroker.pool.GameUserPool;
import com.google.common.collect.Maps;

import java.util.Map;

public class GameWorld extends AbstractBehavior<IGameWorldActor> {

	public static final String IDENTIFY = GameWorld.class.getSimpleName();

	private long gameWorldId;

	Map<String, IGameUser> userMap= Maps.newTreeMap();

	public static Behavior<IGameWorldActor> create(long id) {
		return Behaviors.setup(
				context -> {
					context
							.getSystem()
							.receptionist()
							.tell(Receptionist.register(ServiceKey.create(IGameWorldActor.class, GameWorld.IDENTIFY+"."+id), context.getSelf()));
					return new GameWorld(context,id);
				});
	}


	public GameWorld(ActorContext<IGameWorldActor> context, long id) {
		super(context);
		this.gameWorldId=id;
	}

	@Override
	public Receive<IGameWorldActor> createReceive() {
		return newReceiveBuilder()
				.onMessage(IGameWorldActor.findIGameUserByIdActor.class,this::findGameUserById)
				.onMessage(IGameWorldActor.UserLoginWorld.class,this::userLoginWorld)
				.onMessage(IGameWorldActor.UserLogOutWorld.class,this::logoutWorld)
				.onMessage(IGameWorldActor.SendAllOnlineUserActor.class,this::sendAllOnlineUserMessage)
				.onMessage(IGameWorldActor.SendAllOnlineUserPacket.class,this::sendAllOnlineUserPacket)
				.onMessage(IGameWorldActor.SendActorToService.class,this::sendMessageToService)
				.build();
	}

	private  Behavior<IGameWorldActor> sendMessageToService(IGameWorldActor.SendActorToService message) {
		IServiceActor.HandleMessage handleMessage = new IServiceActor.HandleMessage(message.object);
		getContext().spawnAnonymous(ServiceGuardian.create(message.serviceName,handleMessage));
		return Behaviors.same();
	}

	private Behavior<IGameWorldActor> sendAllOnlineUserPacket(IGameWorldActor.SendAllOnlineUserPacket message) {
		userMap.values().forEach(gameUser -> gameUser.sendMessageToSelf(message.iPacket));
		return Behaviors.same();
	}

	private Behavior<IGameWorldActor> sendAllOnlineUserMessage(IGameWorldActor.SendAllOnlineUserActor message) {
		userMap.values().forEach(gameUser -> gameUser.sendMessageToIoSession(message.requestId,message.message));
		return Behaviors.same();
	}

	private  Behavior<IGameWorldActor> logoutWorld(IGameWorldActor.UserLogOutWorld message) {

		userMap.remove(message.gameUser.getUserId());

		AppListener appListener = ContextResolver.getAppListener();
		if (appListener.handleLogout(message.gameUser)){
			GameUserPool.returnGameUser((GameUser) message.gameUser);
		}
		return Behaviors.same();
	}

	private Behavior<IGameWorldActor> userLoginWorld(IGameWorldActor.UserLoginWorld message) {

		IGameUser gameUser = message.gameUser;
		userMap.put(gameUser.getUserId(), gameUser);

		AppListener appListener = ContextResolver.getAppListener();
		appListener.userLogin(gameUser);


		return Behaviors.same();
	}

	private Behavior<IGameWorldActor> findGameUserById(IGameWorldActor.findIGameUserByIdActor message) {
		if (userMap.containsKey(message.id)){
			message.reply.tell(new IGameWorldActor.FindGameUser(userMap.get(message.id)));
		}else {
			message.reply.tell(IGameWorldActor.NoFindGameUser.INSTANCE);
		}
		return Behaviors.same();
	}
}
