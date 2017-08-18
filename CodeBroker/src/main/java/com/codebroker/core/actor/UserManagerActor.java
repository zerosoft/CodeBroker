package com.codebroker.core.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.codebroker.api.IUser;
import com.codebroker.core.entities.User;
import com.codebroker.core.manager.UserManager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class UserManagerActor extends AbstractActor {

	private final UserManager manager;

	public static final String IDENTIFY = UserManagerActor.class.getSimpleName().toString();

	private static final String USER_PRFIX = "USER-";
	private static final String NPC_PRFIX = "NPC-";
	private static final AtomicInteger USER_ID = new AtomicInteger(1);
	private Map<String, User> userMap = new TreeMap<String, User>();
	private Map<String, String> rebindKeyUserMap = new TreeMap<String, String>();

	public UserManagerActor(UserManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(CreateUser.class, msg -> {
			createUser(msg.npc, null, msg.reBindKey);
		}).match(RemoveUser.class, msg -> {
			removeUser(msg);
		}).match(GetUserList.class, msg -> {
			getUserList(msg);
		}).match(CreateUserWithSession.class, msg -> {
			createUser(false, msg.ioSession, msg.reBindKey);
		}).match(SetReBindKey.class, msg -> {
			rebindKeyUserMap.put(msg.reBindKey, msg.userId);
		}).match(FindUserByRebindKey.class, msg -> {
			processReBind(msg);
		}).match(GetPlayerUser.class, msg -> {
			User user = userMap.get(msg.userId);
			getSender().tell(user, getSelf());
		}).build();
	}

	/**
	 * 处理重连接
	 * 
	 * @param msg
	 */
	private void processReBind(FindUserByRebindKey msg) {
		if (rebindKeyUserMap.containsKey(msg.bindKey)) {
			String key = rebindKeyUserMap.get(msg.bindKey);
			if (userMap.containsKey(key)) {
				User user = userMap.get(key);
				user.rebindIoSession(getSender());
				getSender().tell(new SessionActor.ReBindUser(true), user.getActorRef());
			} else {
				getSender().tell(new SessionActor.ReBindUser(false), ActorRef.noSender());
			}
		} else {
			getSender().tell(new SessionActor.ReBindUser(false), ActorRef.noSender());
		}
	}

	private void getUserList(GetUserList msg) {
		List<IUser> list = new ArrayList<>();
		for (Entry<String, User> iUser : userMap.entrySet()) {
			if (msg.type.equals(GetUserList.Type.ALL)) {
				list.add(iUser.getValue());
				continue;
			} else if (msg.type.equals(GetUserList.Type.PLAYER) && !iUser.getValue().isNpc()) {
				list.add(iUser.getValue());
				continue;
			} else if (msg.type.equals(GetUserList.Type.NPC) && iUser.getValue().isNpc()) {
				list.add(iUser.getValue());
				continue;
			}
		}
		getSender().tell(list, getSelf());
	}

	private void removeUser(RemoveUser msg) {
		userMap.remove(msg.userID);
	}

	private void createUser(boolean npc, ActorRef ioSession, String reBindKey) {
		int id = USER_ID.incrementAndGet();
		User user = new User();
		ActorRef actorOf = null;
		ActorContext context = getContext();

		String userid = npc ? NPC_PRFIX + id : USER_PRFIX + id;
		user.setUserId(userid);
		user.setNpc(npc);

		actorOf = context.actorOf(Props.create(UserActor.class, user, npc, ioSession), userid);
		user.setActorRef(actorOf);

		userMap.put(userid, user);

		getSender().tell(user, getSelf());
	}

	/**
	 * 创建一个用户
	 * 
	 * @author xl
	 *
	 */
	public static class CreateUser {
		public final boolean npc;
		public final String reBindKey;

		public CreateUser(boolean npc, String reBindKey) {
			super();
			this.npc = npc;
			this.reBindKey = reBindKey;
		}
	}

	public static class CreateUserWithSession {
		public final ActorRef ioSession;
		public final String reBindKey;

		public CreateUserWithSession(ActorRef ioSession, String reBindKey) {
			super();
			this.ioSession = ioSession;
			this.reBindKey = reBindKey;
		}

	}

	public static class SetReBindKey {
		public final String reBindKey;
		public final String userId;

		public SetReBindKey(String reBindKey, String userId) {
			super();
			this.reBindKey = reBindKey;
			this.userId = userId;
		}

	}

	public static class RemoveUser {
		public final String userID;

		public RemoveUser(String userID) {
			super();
			this.userID = userID;
		}

	}

	public static class GetUserList {
		public enum Type {
			PLAYER, NPC, ALL
		}

		public final Type type;

		public GetUserList(Type type) {
			super();
			this.type = type;
		}

	}

	public static class FindUserByRebindKey {
		public final String bindKey;

		public FindUserByRebindKey(String bindKey) {
			super();
			this.bindKey = bindKey;
		}

	}

	public static class GetPlayerUser {

		public final String userId;

		public GetPlayerUser(String userId) {
			super();
			this.userId = userId;
		}

	}
}
