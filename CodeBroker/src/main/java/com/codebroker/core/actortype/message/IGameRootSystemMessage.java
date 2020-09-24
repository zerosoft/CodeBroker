package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import com.codebroker.api.internal.IBindingActor;

/**
 * 游戏世界 相关通讯协议
 *
 * @author LongJu
 * @Date 2020/3/25
 */
public interface IGameRootSystemMessage {
    /**
     * 会话登入
     */
    final class SessionOpen implements IGameRootSystemMessage {
        public IBindingActor<ISession> ioSession;

        public SessionOpen(IBindingActor<ISession> ioSession) {
            this.ioSession = ioSession;
        }
    }

    /**
     * 启动游戏世界
     */
    class StartGameRootSystemMessage implements IGameRootSystemMessage {
        public final ActorRef<Reply> replyTo;

        public StartGameRootSystemMessage(ActorRef<Reply> replyTo) {
            this.replyTo = replyTo;
        }
    }



    /**
     * 重启游戏世界
     */
    enum ReStartGameRootSystemMessage implements IGameRootSystemMessage {
        INSTANCE;
    }

    /**
     * 停止游戏世界
     */
    enum StopGameRootSystemMessage implements IGameRootSystemMessage {
        INSTANCE;
    }


    /**
     * 创建service消息，同步等待创建
     */
    class CreateService implements IGameRootSystemMessage {

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
    class createGlobalService implements IGameRootSystemMessage {

        public String name;
        public com.codebroker.api.internal.IService service;
        public ActorRef<Reply> replyTo;

        public createGlobalService(String name, com.codebroker.api.internal.IService service, ActorRef<Reply> replyTo) {
            this.service = service;
            this.name = name;
            this.replyTo = replyTo;
        }
    }
    class createClusterService implements IGameRootSystemMessage {

        public String name;
        public com.codebroker.api.internal.IService service;
        public ActorRef<Reply> replyTo;

        public createClusterService(String name, com.codebroker.api.internal.IService service, ActorRef<Reply> replyTo) {
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

    final class StartWorldFinish implements Reply {

    }
}
