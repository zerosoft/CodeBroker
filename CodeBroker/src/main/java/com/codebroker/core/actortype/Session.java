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
import com.codebroker.core.actortype.message.IUserManager;

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
    long gameWorldId;

    public static Behavior<ISession> create(long sessionId, IoSession ioSession, long gameWorldId) {
        Behavior<ISession> setup = Behaviors.setup(context->new Session(context,sessionId,ioSession,gameWorldId));
        return setup;
    }

    public Session(ActorContext<ISession> context,long sessionId, IoSession ioSession, long gameWorldId) {
        super(context);
        this.sessionId=sessionId;
        this.ioSession=ioSession;
        this.gameWorldId=gameWorldId;
    }

    @Override
    public Receive<ISession> createReceive() {
        return newReceiveBuilder()
                .onMessage(ISession.SessionAcceptRequest.class,this::sessionAcceptMessage)
                .onMessage(ISession.SessionWriteResponse.class,this::sessionSendMessage)
                .onMessage(ISession.SessionBindingUser.class,this::sessionBindingUser)
                .onMessage(ISession.TryBindingUserFail.class,this::tryBindingUserFail)
                .onMessage(ISession.SessionClose.class,this::sessionClose)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<ISession> tryBindingUserFail(ISession.TryBindingUserFail message) {
        getContext().getSystem().log().info("tryBindingUserFail fail");
        return Behaviors.stopped();
    }

    private  Behavior<ISession> sessionSendMessage(ISession.SessionWriteResponse message) {
        ioSession.write(message.response);
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


    private Behavior<ISession> sessionAcceptMessage(ISession.SessionAcceptRequest message) {
        getContext().getLog().debug("Session accept Message {}",message.request.getOpCode());
        if (userActorRef==null)
        {
            getContext().spawnAnonymous(
                    UserManagerGuardian.create(
                    		new IUserManager.TryBindingUser(getContext().getSelf(), message.request)
							,(int) gameWorldId
                    )
            );
        }else{
            userActorRef.tell(new IUser.ReceiveMessageFromSession(message.request));
        }
        return Behaviors.same();
    }
}
