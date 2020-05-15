package com.codebroker.core.actortype.message;

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
        NO_INSTANCE;
    }
    /**
     * 重启游戏世界
     */
    final class ReStartWorldMessage implements IWorldMessage {
        public static ReStartWorldMessage getInstance() {
            return new ReStartWorldMessage();
        }
    }
    /**
     * 停止游戏世界
     */
    final class StopWorldMessage implements IWorldMessage {
        public static StopWorldMessage getInstance() {
            return new StopWorldMessage();
        }
    }

    class ListingResponse implements IWorldMessage {
        final Receptionist.Listing listing;

        public ListingResponse(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }

    class CreateService implements IWorldMessage{

        public String name;
        public IService service;

        public CreateService(String name,IService service ) {
            this.service = service;
            this.name = name;
        }
    }
}
