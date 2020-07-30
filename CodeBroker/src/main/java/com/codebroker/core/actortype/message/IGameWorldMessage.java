package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.api.IGameUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.GameUser;
import com.codebroker.protocol.SerializableType;
import com.fasterxml.jackson.annotation.JsonCreator;

public interface IGameWorldMessage {
	/**
	 * 添加处理器参考
	 */
	final class AddProcessorReference implements IGameWorldMessage {

		public Receptionist.Listing listing;

		public AddProcessorReference(Receptionist.Listing listing) {
			this.listing = listing;
		}
	}

	/**
	 * 通过ID查找角色
	 */
	final class findIGameUserByIdMessage implements IGameWorldMessage {

		public final String id;
		public final ActorRef<IGameWorldMessage.Reply> reply;

		public findIGameUserByIdMessage(String id, ActorRef<Reply> reply) {
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
	class UserLoginWorld implements IGameWorldMessage {

		public final IGameUser gameUser;

		public UserLoginWorld(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}
	/**
	 * 角色离开到世界
	 */
	class UserLogOutWorld implements IGameWorldMessage {
		public final IGameUser gameUser;

		public UserLogOutWorld(GameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	class SendAllOnlineUserMessage implements IGameWorldMessage {
		public final int requestId;
		public final Object message;
		public SendAllOnlineUserMessage(int requestId, Object message) {
			this.requestId=requestId;
			this.message=message;
		}
	}

	/**
	 * 给所有在线的玩家发事件
	 */
	class SendAllOnlineUserEvent implements IGameWorldMessage {
		public final IEvent event;
		public SendAllOnlineUserEvent(IEvent event) {
			this.event=event;
		}
	}

	/**
	 * 消息发送给服务
	 */
	class SendMessageToService implements IGameWorldMessage {
		public final String serviceName;

		public final IObject object;

		@JsonCreator
		public SendMessageToService(String serviceName, IObject object) {
			this.serviceName=serviceName;
			this.object=object;
		}
	}
}
