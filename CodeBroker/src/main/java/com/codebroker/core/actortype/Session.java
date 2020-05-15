package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.api.IoSession;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.IUser;

/**
 * 会话代理的actor
 * @author LongJu
 * @Date 2020/3/26
 */
public class Session extends AbstractBehavior<ISession> {

    public static final String IDENTIFY = Session.class.getSimpleName();
    //绑定用户Actor
    ActorRef<IUser> userActorRef;

    long sessionId;
    IoSession ioSession;

    public static Behavior<ISession> create(long sessionId, IoSession ioSession) {
        Behavior<ISession> setup = Behaviors.setup(context->new Session(context,sessionId,ioSession));
        return setup;
    }

    public Session(ActorContext<ISession> context,long sessionId, IoSession ioSession) {
        super(context);
        this.sessionId=sessionId;
        this.ioSession=ioSession;
    }

    @Override
    public Receive<ISession> createReceive() {
        return newReceiveBuilder()
                .onMessage(ISession.SessionAcceptMessage.class,this::sessionAcceptMessage)
                .onMessage(ISession.SessionSendMessage.class,this::sessionSendMessage)
                .onMessage(ISession.SessionBindingUser.class,this::sessionBindingUser)
                .onMessage(ISession.SessionClose.class,this::sessionClose)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private  Behavior<ISession> sessionSendMessage(ISession.SessionSendMessage message) {
        ioSession.write(message.message);
        return Behaviors.same();
    }


    private  Behavior<ISession> sessionBindingUser(ISession.SessionBindingUser message) {
        this.userActorRef=message.userActorRef;
        return Behaviors.same();
    }

    /**
     * 会在 stopSession 前执行
     * @return
     */
    private Behavior<ISession> onPostStop() {
        if (userActorRef!=null){
            userActorRef.tell(IUser.SessionClose.INSTANCE);
        }
        getContext().getSystem().log().info("sessionClose onPostStop stopped");
        return Behaviors.same();
    }

    /**
     * 关闭服务
     * @param message
     * @return
     */
    private Behavior<ISession> sessionClose(ISession.SessionClose message) {
        if (message.enforce){
            userActorRef=null;
            ioSession.close(true);
        }
        return Behaviors.stopped(()->getContext().getSystem().log().info("sessionClose up"));
    }


    private Behavior<ISession> sessionAcceptMessage(ISession.SessionAcceptMessage message) {
        getContext().getLog().debug("Session accept Message {}",message.message);
        if (userActorRef==null){
            getContext().spawnAnonymous(UserManagerGuardian.create(getContext().getSelf(),message));
        }else{
            userActorRef.tell(new IUser.ReceiveMessageFromSession(message.message));
        }
        return Behaviors.same();
    }
}
