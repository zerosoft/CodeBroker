package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.api.CodeBrokerAppListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.core.entities.GameUser;

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
                .onMessage(IUser.LogicEvent.class, this::handlerLogicEvent)
                .build();
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
        gameUser = new GameUser(uid, getContext().getSelf());
        CodeBrokerAppListener appListener = ContextResolver.getAppListener();
        appListener.userLogin(gameUser);
        return Behaviors.same();
    }

    private Behavior<IUser> disconnect(IUser.Disconnect message) {
        boolean enforce = message.enforce;
        if (enforce) {
            ioSession.tell(new ISession.SessionClose(enforce));
            ioSession = null;
        }
        if (gameUser != null) {
            CodeBrokerAppListener appListener = ContextResolver.getAppListener();
            appListener.handleLogout(gameUser);
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
        CodeBrokerAppListener appListener = ContextResolver.getAppListener();
        try {
            int opCode = message.message.getOpCode();
            appListener.handleClientRequest(gameUser, opCode, message.message.getRawData());
        } catch (Exception e) {
            getContext().getLog().error("handleClientRequest error ", e);
        }
        return Behaviors.same();
    }
}
