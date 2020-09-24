package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.*;
import akka.pattern.StatusReply;
import com.codebroker.api.AppListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.*;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.entities.GameUser;
import com.codebroker.pool.GameUserPool;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

/**
 * 用户Actor
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public class User extends AbstractBehavior<IUser> {

    public static final String IDENTIFY = User.class.getSimpleName();

    private String uid;
    private ActorRef<IUserManager> parent;
    private ActorRef<ISession> ioSession;
    private GameUser gameUser;
    private final Executor ec;

    public static Behavior<IUser> create(String uid, ActorRef<ISession> ioSession, ActorRef<IUserManager> parent) {
        Behavior<IUser> setup = Behaviors.setup(context -> new User(context, uid, ioSession, parent));

        return setup;
    }

    public User(ActorContext<IUser> context, String uid, ActorRef<ISession> ioSession, ActorRef<IUserManager> parent) {
        super(context);
        this.uid = uid;
        this.ioSession = ioSession;
        this.parent = parent;
        ec = context.getSystem().dispatchers().lookup(DispatcherSelector.fromConfig("game-logic"));
    }

    @Override
    public Receive<IUser> createReceive() {
        return newReceiveBuilder()
                .onMessage(IUser.ReceiveMessageFromSession.class, this::getMessageFromSession)
                .onMessage(IUser.NewSessionLogin.class, this::newSessionLogin)
                .onMessage(IUser.SessionClose.class, this::sessionClose)
                .onMessage(IUser.Disconnect.class, this::disconnect)
                .onMessage(IUser.NewGameUserInit.class, this::newGameUserInit)
                .onMessage(IUser.SendMessageToSession.class, this::sendMessageToSession)
                .onMessage(IUser.SendMessageToIService.class, this::sendMessageToIService)
                .onMessage(IUser.LogicEvent.class, this::handlerLogicEvent)
                .build();
    }

    private Behavior<IUser> sendMessageToIService(IUser.SendMessageToIService message) {
        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();

        if (ActorPathService.localService.containsKey(message.serviceName)) {
            CompletionStage<IService.Reply> ask = AskPattern.askWithStatus(
                    ActorPathService.localService.get(message.serviceName),
                    replyActorRef -> new IService.HandleUserMessage(message.message, replyActorRef),
                    Duration.ofMillis(3),
                    actorSystem.scheduler());
            ask.whenComplete((reply, throwable) -> {
                if (reply instanceof IService.HandleUserMessageBack) {
                    message.replyTo.tell(new IUser.IObjectReply(((IService.HandleUserMessageBack) reply).object));
                }
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            });
            ask.whenComplete(
                    (reply, failure) -> {
                        if (reply instanceof IService.HandleUserMessageBack) {
                            message.replyTo.tell(new IUser.IObjectReply(((IService.HandleUserMessageBack) reply).object));
                        } else if (failure instanceof StatusReply.ErrorMessage) {
                            message.replyTo.tell(new IUser.IObjectReply(CObjectLite.newInstance()));
                        }
                    });
        }
        return Behaviors.same();
    }

    private Behavior<IUser> handlerLogicEvent(IUser.LogicEvent message) {
        gameUser.handlerEvent(message.event);
        return Behaviors.same();
    }

    private Behavior<IUser> sendMessageToSession(IUser.SendMessageToSession message) {
        if (ioSession!=null){
            ioSession.tell(new ISession.SessionWriteResponse(message.message));
        }
        return Behaviors.same();
    }

    private Behavior<IUser> newGameUserInit(IUser.NewGameUserInit message) {
        //进入游戏的
        this.gameUser = GameUserPool.getGameUser(uid,getContext().getSelf());

        getContext().spawnAnonymous(GameWorldGuardian.create(new IGameWorldMessage.UserLoginWorld(this.gameUser)));

        return Behaviors.same();
    }

    private Behavior<IUser> disconnect(IUser.Disconnect message) {
        boolean enforce = message.enforce;
        if (enforce) {
            ioSession.tell(new ISession.SessionClose(enforce));
            ioSession = null;
        }
        if (gameUser != null) {
            getContext().spawnAnonymous(GameWorldGuardian.create(new IGameWorldMessage.UserLogOutWorld(gameUser)));
        }
        return Behaviors.stopped();
    }

    private Behavior<IUser> sessionClose(Object message) {
        getContext().getLog().debug("User lost session");
        this.ioSession = null;
        parent.tell(new IUserManager.UserLostSession(getContext().getSelf()));
        return Behaviors.same();
    }

    private Behavior<IUser> newSessionLogin(IUser.NewSessionLogin message) {
        if (ioSession != null) {
            ioSession.tell(new ISession.SessionClose(true));
        }
        ioSession = message.iSessionActorRef;
        return Behaviors.same();
    }

    private Behavior<IUser> getMessageFromSession(IUser.ReceiveMessageFromSession message) {
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
