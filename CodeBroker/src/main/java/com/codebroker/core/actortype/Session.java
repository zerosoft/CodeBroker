package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.codebroker.api.IoSession;
import com.codebroker.core.actortype.message.ISessionActor;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.core.actortype.message.IUserManager;

/**
 * 会话代理的actor
 * @author LongJu
 * @Date 2020/3/26
 */
public class Session extends AbstractBehavior<ISessionActor> {

    public static final String IDENTIFY = Session.class.getSimpleName();
    //绑定用户Actor
    ActorRef<IUserActor> userActorRef;

    long sessionId;
    IoSession ioSession;
    long gameWorldId;

    public static Behavior<ISessionActor> create(long sessionId, IoSession ioSession, long gameWorldId) {
        Behavior<ISessionActor> setup = Behaviors.setup(context->new Session(context,sessionId,ioSession,gameWorldId));
        return setup;
    }

    public Session(ActorContext<ISessionActor> context, long sessionId, IoSession ioSession, long gameWorldId) {
        super(context);
        this.sessionId=sessionId;
        this.ioSession=ioSession;
        this.gameWorldId=gameWorldId;
    }

    @Override
    public Receive<ISessionActor> createReceive() {
        return newReceiveBuilder()
                .onMessage(ISessionActor.SessionActorAcceptRequest.class,this::sessionAcceptMessage)
                .onMessage(ISessionActor.SessionActorWriteResponse.class,this::sessionSendMessage)
                .onMessage(ISessionActor.SessionActorBindingUser.class,this::sessionBindingUser)
                .onMessage(ISessionActor.TryBindingUserFail.class,this::tryBindingUserFail)
                .onMessage(ISessionActor.SessionActorClose.class,this::sessionClose)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<ISessionActor> tryBindingUserFail(ISessionActor.TryBindingUserFail message) {
        getContext().getSystem().log().info("tryBindingUserFail fail");
        return Behaviors.stopped();
    }

    private  Behavior<ISessionActor> sessionSendMessage(ISessionActor.SessionActorWriteResponse message) {
        ioSession.write(message.response);
        return Behaviors.same();
    }


    private  Behavior<ISessionActor> sessionBindingUser(ISessionActor.SessionActorBindingUser message) {
        this.userActorRef=message.userActorRef;
        return Behaviors.same();
    }

    /**
     * 会在 stopSession 前执行
     * @return
     */
    private Behavior<ISessionActor> onPostStop() {
        if (userActorRef!=null){
            userActorRef.tell(IUserActor.SessionClose.INSTANCE);
        }
        getContext().getSystem().log().info("sessionClose onPostStop stopped");
        return Behaviors.same();
    }

    /**
     * 关闭服务
     * @param message
     * @return
     */
    private Behavior<ISessionActor> sessionClose(ISessionActor.SessionActorClose message) {
        if (message.enforce){
            userActorRef=null;
            ioSession.close(true);
        }
        return Behaviors.stopped(()->getContext().getSystem().log().info("sessionClose up"));
    }


    private Behavior<ISessionActor> sessionAcceptMessage(ISessionActor.SessionActorAcceptRequest message) {
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
            userActorRef.tell(new IUserActor.ReceiveMessageFromSession(message.request));
        }
        return Behaviors.same();
    }
}
