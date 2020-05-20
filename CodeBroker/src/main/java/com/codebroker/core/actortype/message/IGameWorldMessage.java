package com.codebroker.core.actortype.message;


import akka.actor.typed.ActorRef;
import com.codebroker.api.IGameUser;

public interface IGameWorldMessage {

	final class findIGameUserByIdMessage implements IGameWorldMessage {

		public final  String id;
		public final  ActorRef<IGameWorldMessage.Reply> reply;

		public findIGameUserByIdMessage(String id, ActorRef<Reply> reply) {
			this.id = id;
			this.reply = reply;
		}
	}

	interface Reply {}

	class FindGameUser implements Reply {

		public final IGameUser gameUser;

		public FindGameUser(IGameUser gameUser) {
			this.gameUser = gameUser;
		}
	}

	enum  NoFindGameUser implements Reply {
		INSTANCE;
	}
}
