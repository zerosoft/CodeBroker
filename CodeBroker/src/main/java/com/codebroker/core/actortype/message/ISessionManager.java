package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.IoSession;

/**
 * description
 *
 * @author LongJu
 * @Date 2020/3/26
 */
public interface ISessionManager {
    /**
     * 创建一个网络session的执行Actor
     */
    final class SessionOpen implements ISessionManager{

        public final ActorRef<IGameRootSystemMessage.Reply> replyTo;
        public final IoSession ioSession;

        public SessionOpen(ActorRef<IGameRootSystemMessage.Reply> replyTo, IoSession ioSession) {
            this.replyTo=replyTo;
            this.ioSession=ioSession;
        }
    }

    /**
     * 关闭网络对应的session actor
     */
    final class SessionClose implements ISessionManager{
        public long sessionId;

        public SessionClose(long sessionId) {
            this.sessionId = sessionId;
        }
    }


    final class RestartSessionManager implements ISessionManager {}

    final class StopSessionManager implements ISessionManager {}



}
