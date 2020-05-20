package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import com.codebroker.api.IGameUser;
import com.codebroker.core.entities.GameUser;

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

	class FindGameUser implements Reply {

		public final IGameUser gameUser;

		public FindGameUser(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	enum NoFindGameUser implements Reply {
		INSTANCE;
	}

	class UserLoginWorld implements IGameWorldMessage {

		public final IGameUser gameUser;

		public UserLoginWorld(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	class UserLogOutWorld implements IGameWorldMessage {
		public final IGameUser gameUser;

		public UserLogOutWorld(GameUser gameUser) {
			this.gameUser = gameUser;
		}
	}
}
