package com.codebroker.core.actortype.message;

import com.codebroker.api.internal.IBindingActor;

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
        public IBindingActor<ISession> ioSession;

        public SessionOpen(IBindingActor<ISession> ioSession) {
            this.ioSession = ioSession;
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
