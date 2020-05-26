package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.internal.IBindingActor;
import com.codebroker.protocol.SerializableType;

/**
 * 游戏世界 相关通讯协议
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IWorldMessage {
    /**
     * 会话登入
     */
    final class SessionOpen implements IWorldMessage {
        public IBindingActor<ISession> ioSession;

        public SessionOpen(IBindingActor<ISession> ioSession) {
            this.ioSession = ioSession;
        }
    }

    /**
     * 启动游戏世界
     */
    enum StartWorldMessage implements IWorldMessage {
        INSTANCE;
    }

    /**
     * 重启游戏世界
     */
    enum ReStartWorldMessage implements IWorldMessage {
        INSTANCE;
    }

    /**
     * 停止游戏世界
     */
    enum StopWorldMessage implements IWorldMessage {
        INSTANCE;
    }


    /**
     * 创建service消息，同步等待创建
     */
    class CreateService implements IWorldMessage {

        public String name;
        public com.codebroker.api.internal.IService service;
        public ActorRef<Reply> replyTo;

        public CreateService(String name, com.codebroker.api.internal.IService service, ActorRef<Reply> replyTo) {
            this.service = service;
            this.name = name;
            this.replyTo = replyTo;
        }
    }

    /**
     * 创建service消息，同步等待创建
     */
    class createGlobalService implements IWorldMessage {

        public String name;
        public com.codebroker.api.internal.IService service;
        public ActorRef<Reply> replyTo;

        public createGlobalService(String name, com.codebroker.api.internal.IService service, ActorRef<Reply> replyTo) {
            this.service = service;
            this.name = name;
            this.replyTo = replyTo;
        }
    }

    interface Reply {
    }

    final class ReplyCreateService implements Reply {
        public final ActorRef<IService> serviceActorRef;

        public ReplyCreateService(ActorRef<IService> serviceActorRef) {
            this.serviceActorRef = serviceActorRef;
        }
    }
}
