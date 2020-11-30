package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.internal.IPacket;
import com.codebroker.core.entities.GameUser;

public interface IGameWorldActor {
	/**
	 * 添加处理器参考
	 */
	final class AddProcessorReference implements IGameWorldActor {

		public Receptionist.Listing listing;

		public AddProcessorReference(Receptionist.Listing listing) {
			this.listing = listing;
		}
	}

	/**
	 * 通过ID查找角色
	 */
	final class findIGameUserByIdActor implements IGameWorldActor {

		public final String id;
		public final ActorRef<IGameWorldActor.Reply> reply;

		public findIGameUserByIdActor(String id, ActorRef<Reply> reply) {
			this.id = id;
			this.reply = reply;
		}
	}

	interface Reply {
	}

	/**
	 * 查找角色的信息返回
	 */
	class FindGameUser implements Reply {

		public final IGameUser gameUser;

		public FindGameUser(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	enum NoFindGameUser implements Reply {
		INSTANCE;
	}

	/**
	 * 角色登入到世界
	 */
	class UserLoginWorld implements IGameWorldActor {

		public final IGameUser gameUser;

		public UserLoginWorld(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}
	/**
	 * 角色离开到世界
	 */
	class UserLogOutWorld implements IGameWorldActor {
		public final IGameUser gameUser;

		public UserLogOutWorld(GameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	class SendAllOnlineUserActor implements IGameWorldActor {
		public final int requestId;
		public final Object message;
		public SendAllOnlineUserActor(int requestId, Object message) {
			this.requestId=requestId;
			this.message=message;
		}
	}

	/**
	 * 给所有在线的玩家发事件
	 */
	class SendAllOnlineUserEvent implements IGameWorldActor {
		public final IEvent event;
		public SendAllOnlineUserEvent(IEvent event) {
			this.event=event;
		}
	}

	/**
	 * 消息发送给服务
	 */
	class SendActorToService implements IGameWorldActor {
		public final String serviceName;

		public final IPacket object;

		public SendActorToService(String serviceName, IPacket object) {
			this.serviceName=serviceName;
			this.object=object;
		}
	}
}
