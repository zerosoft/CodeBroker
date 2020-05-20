package com.codebroker.core.actortype.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.api.internal.IBindingActor;
import com.codebroker.api.internal.IService;

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
     enum  ReStartWorldMessage implements IWorldMessage {
        INSTANCE;
    }
    /**
     * 停止游戏世界
     */
    enum StopWorldMessage implements IWorldMessage {
        INSTANCE;
    }

    class ListingResponse implements IWorldMessage {
        final Receptionist.Listing listing;
        public ListingResponse(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }

    /**
     * 创建service消息，同步等待创建
     */
    class CreateService implements IWorldMessage{

        public String name;
        public IService service;
        public ActorRef<Reply> replyTo;

        public CreateService(String name,IService service,ActorRef<Reply> replyTo) {
            this.service = service;
            this.name = name;
            this.replyTo=replyTo;
        }
    }

    interface Reply {}

    enum ReplyCreateService implements Reply{
        INSTANCE;
    }
}
