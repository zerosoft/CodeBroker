package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.IoSession;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.ISessionManager;
import com.codebroker.core.actortype.message.IUserManager;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 会话管理器
 *
 * @author LongJu
 * @Date 2020/3/26
 */
public class SessionManager extends AbstractBehavior<ISessionManager> {

    public static final String IDENTIFY = SessionManager.class.getSimpleName();
    /**
     * 创建的Session管理
     */
    Map<Long, ActorRef<ISession>> sessions= Maps.newHashMap();
    /**
     * Id 生成使用
     */
    long idGenerator =1L;
    long gameWorldId;

    public static Behavior<ISessionManager> create(long gameWorldId) {
        return Behaviors.setup(
                context -> {
                    context
                            .getSystem()
                            .receptionist()
                            .tell(Receptionist.register(ServiceKey.create(ISessionManager.class, SessionManager.IDENTIFY+"."+gameWorldId), context.getSelf()));
                    return new SessionManager(context,gameWorldId);
                });
    }

    public SessionManager(ActorContext<ISessionManager> context,long gameWorldId) {
        super(context);
        this.gameWorldId=gameWorldId;
        getContext().getSystem().log().info("SessionManager start");
    }

    @Override
    public Receive<ISessionManager> createReceive() {
        return newReceiveBuilder()
                .onMessage(ISessionManager.SessionOpen.class,this::sessionOpen)
                .onMessage(ISessionManager.SessionClose.class,this::sessionClose)
                .build();
    }

    private Behavior<ISessionManager> sessionOpen(ISessionManager.SessionOpen message) {
        getContext().getLog().debug("SessionManager createSession session");
        //创建一个新的id自增
        long sessionId = idGenerator++;
        ActorRef<ISession> session = getContext().spawn(Session.create(sessionId, message.ioSession,gameWorldId), Session.IDENTIFY+"."+ sessionId);
        getContext().getLog().debug("session path {}",session.path());

        //加入监听
        getContext().watchWith(session,new ISessionManager.SessionClose(sessionId));
        sessions.put(sessionId,session);
        //通知给调用Session
        message.replyTo.tell(new IGameRootSystemMessage.SessionOpenReply(session));

        return Behaviors.same();
    }

    private Behavior<ISessionManager> sessionClose(ISessionManager.SessionClose message) {
        getContext().getLog().debug("session Close manager session Id {}",message.sessionId);
        sessions.remove(message.sessionId);
        return Behaviors.same();
    }

}
