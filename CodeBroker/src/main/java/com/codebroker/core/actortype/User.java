package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.pattern.StatusReply;
import com.codebroker.api.AppContext;
import com.codebroker.api.AppListener;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.Event;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.*;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.GameUser;
import com.codebroker.pool.GameUserPool;
import com.codebroker.api.event.EventName;
import com.codebroker.setting.SystemEnvironment;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

/**
 * 用户Actor
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public class User extends AbstractBehavior<IUserActor> {

    public static final String IDENTIFY = User.class.getSimpleName();

    private String uid;
    private ActorRef<IUserManager> parent;
    private ActorRef<ISessionActor> ioSession;
    private GameUser gameUser;
    private final Executor ec;

    public static Behavior<IUserActor> create(String uid, ActorRef<ISessionActor> ioSession, ActorRef<IUserManager> parent) {
        Behavior<IUserActor> setup = Behaviors.setup(context -> new User(context, uid, ioSession, parent));
        return setup;
    }

    public User(ActorContext<IUserActor> context, String uid, ActorRef<ISessionActor> ioSession, ActorRef<IUserManager> parent) {
        super(context);
        this.uid = uid;
        this.ioSession = ioSession;
        this.parent = parent;
        ec = context.getSystem().dispatchers().lookup(DispatcherSelector.fromConfig("game-logic"));
    }

    @Override
    public Receive<IUserActor> createReceive() {
        return newReceiveBuilder()
                .onMessage(IUserActor.ReceiveMessageFromSession.class, this::getMessageFromSession)
                .onMessage(IUserActor.NewSessionLogin.class, this::newSessionLogin)
                .onMessage(IUserActor.SessionClose.class, this::sessionClose)
                .onMessage(IUserActor.Disconnect.class, this::disconnect)
                .onMessage(IUserActor.NewGameUserActorInit.class, this::newGameUserInit)
                .onMessage(IUserActor.SendMessageToSession.class, this::sendMessageToSession)
				.onMessage(IUserActor.SendMessageToGameUserActor.class,this::sendMessageToGameUser)
				.onMessage(IUserActor.GetSendMessageToGameUserActor.class,this::getSendMessageToGameUser)
                .onMessage(IUserActor.SendMessageToIServiceActor.class, this::sendMessageToIService)
                .onMessage(IUserActor.LogicEvent.class, this::handlerLogicEvent)
                .onSignal(PostStop.class, this::postStop)
                .build();
    }

    private Behavior<IUserActor> postStop(PostStop message) {
        if (gameUser!=null){
            gameUser.handlerEvent(new Event(IGameUser.UserEvent.LOGOUT.name(),null));
        }
        return Behaviors.same();
    }

    private  Behavior<IUserActor> getSendMessageToGameUser(IUserActor.GetSendMessageToGameUserActor message) {
		gameUser.dispatchEvent(new Event(EventName.GAME_EVENT,message.message));
		return Behaviors.same();
	}

	private  Behavior<IUserActor> sendMessageToGameUser(IUserActor.SendMessageToGameUserActor message) {
		getContext().spawnAnonymous(
				UserManagerGuardian.create(
				        new IUserManager.SendMessageToGameUser(message.userId,message.message,getContext().getSelf())
                        , AppContext.getServerId()
                )
        );
		return Behaviors.same();
	}

	private Behavior<IUserActor> sendMessageToIService(IUserActor.SendMessageToIServiceActor message) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

        if (ActorPathService.localService.containsKey(message.serviceName)) {

            CompletionStage<IServiceActor.Reply> ask = AskPattern.askWithStatus(
                    ActorPathService.localService.get(message.serviceName),
                    replyActorRef -> new IServiceActor.HandleUserMessage(message.message, replyActorRef),
                    Duration.ofMillis(SystemEnvironment.TIME_OUT_MILLIS),
                    actorSystem.scheduler());

            ask.whenComplete((reply, throwable) -> {
                if (reply instanceof IServiceActor.HandleUserMessageBack) {
                    message.replyTo.tell(new IUserActor.IObjectReply(((IServiceActor.HandleUserMessageBack) reply).object));
                }
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
            ask.whenComplete(
                    (reply, failure) -> {
                        if (reply instanceof IServiceActor.HandleUserMessageBack) {
                            message.replyTo.tell(new IUserActor.IObjectReply((IObject)((IServiceActor.HandleUserMessageBack) reply).object));
                        } else if (failure instanceof StatusReply.ErrorMessage) {
                            message.replyTo.tell(new IUserActor.IObjectReply(CObjectLite.newInstance()));
                        }
                    });
        }
        return Behaviors.same();
    }

    private Behavior<IUserActor> handlerLogicEvent(IUserActor.LogicEvent message) {
        try {
            gameUser.handlerEvent(message.event);
        }catch (Exception e){
            getContext().getLog().error("handlerLogicEvent error ", e);
        }
        return Behaviors.same();
    }

    private Behavior<IUserActor> sendMessageToSession(IUserActor.SendMessageToSession message) {
        if (ioSession!=null){
            ioSession.tell(new ISessionActor.SessionActorWriteResponse(message.message));
        }
        return Behaviors.same();
    }

    private Behavior<IUserActor> newGameUserInit(IUserActor.NewGameUserActorInit message) {
        //进入游戏的
        this.gameUser = GameUserPool.getGameUser(uid,getContext().getSelf());

        getContext().spawnAnonymous(GameWorldGuardian.create(new IGameWorldActor.UserLoginWorld(this.gameUser)));

        return Behaviors.same();
    }

    private Behavior<IUserActor> disconnect(IUserActor.Disconnect message) {
        boolean enforce = message.enforce;
        if (enforce) {
            if (ioSession!=null){
                ioSession.tell(new ISessionActor.SessionActorClose(enforce));
                ioSession = null;
            }
        }
        if (gameUser != null) {
            getContext().spawnAnonymous(GameWorldGuardian.create(new IGameWorldActor.UserLogOutWorld(gameUser)));
        }
        return Behaviors.stopped();
    }

    private Behavior<IUserActor> sessionClose(Object message) {
        getContext().getLog().debug("User lost session");
        this.ioSession = null;
        parent.tell(new IUserManager.UserLostSession(getContext().getSelf()));
        return Behaviors.same();
    }

    private Behavior<IUserActor> newSessionLogin(IUserActor.NewSessionLogin message) {
        if (ioSession != null) {
            ioSession.tell(new ISessionActor.SessionActorClose(true));
        }
        ioSession = message.iSessionActorRef;
        return Behaviors.same();
    }

    private Behavior<IUserActor> getMessageFromSession(IUserActor.ReceiveMessageFromSession message) {
        AppListener appListener = ContextResolver.getAppListener();
        try {
            int opCode = message.message.getOpCode();
            appListener.handleClientRequest(gameUser, opCode, message.message.getRawData());
        } catch (Exception e) {
            getContext().getLog().error("handleClientRequest error ", e);
        }
        return Behaviors.same();
    }
}
