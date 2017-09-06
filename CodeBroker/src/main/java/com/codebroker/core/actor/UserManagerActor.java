package com.codebroker.core.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.codebroker.api.IUser;
import com.codebroker.core.entities.User;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.session.ReBindUser;
import com.message.thrift.actor.usermanager.CreateUser;
import com.message.thrift.actor.usermanager.CreateUserWithSession;
import com.message.thrift.actor.usermanager.RemoveUser;
import com.message.thrift.actor.world.CreateUserResult;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class UserManagerActor extends AbstractActor {


	public static final String IDENTIFY = UserManagerActor.class.getSimpleName().toString();

	private static final String USER_PRFIX = "USER-";
	private static final String NPC_PRFIX = "NPC-";
	private static final AtomicInteger USER_ID = new AtomicInteger(1);
	private Map<String, User> userMap = new TreeMap<String, User>();
	private Map<String, String> rebindKeyUserMap = new TreeMap<String, String>();
	private final ActorRef worldRef;

	public UserManagerActor(ActorRef worldRef) {
		super();
		this.worldRef = worldRef;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
		 .match(byte[].class, msg -> {
			ActorMessage actorMessage = ThriftSerializerFactory.getActorMessage(msg);
			switch (actorMessage.op) 
			{
			case USER_MANAGER_CREATE_USER:
				CreateUser createUser=new CreateUser();
				ThriftSerializerFactory.deserialize(createUser, actorMessage.messageRaw);
				createUser(createUser.npc, null, createUser.reBindKey);
				break;
			case USER_MANAGER_REMOVE_USER:
				RemoveUser removeUser=new RemoveUser();
				ThriftSerializerFactory.deserialize(removeUser, actorMessage.messageRaw);
				removeUser(removeUser.userID);
				break;
			case USER_MANAGER_CREATE_USER_WITH_SESSION:
				CreateUserWithSession createUserWithSession=new CreateUserWithSession();
				ThriftSerializerFactory.deserialize(createUserWithSession, actorMessage.messageRaw);
				createWorldUser(createUserWithSession.reBindKey,getSender());
				createUser(false, getSender(), createUserWithSession.reBindKey);
				break;
			default:

				break;
			}
		})
		 .match(GetUserList.class, msg -> {
			getUserList(msg);
		})
//		 .match(CreateUserWithSession.class, msg -> {
//			createUser(false, msg.ioSession, msg.reBindKey);
//		})
		 .match(SetReBindKey.class, msg -> {
			rebindKeyUserMap.put(msg.reBindKey, msg.userId);
		}).match(FindUserByRebindKey.class, msg -> {
			processReBind(msg);
		}).match(GetPlayerUser.class, msg -> {
			User user = userMap.get(msg.userId);
			getSender().tell(user, getSelf());
		}).build();
	}

	private void createWorldUser(String reBindKey, ActorRef sender) {
		int id = USER_ID.incrementAndGet();
		User user = new User();
		ActorRef actorOf = null;
		ActorContext context = getContext();

		String userid = USER_PRFIX + id;
		user.setUserId(userid);
		user.setNpc(false);
		CreateUserResult createUserResult=new CreateUserResult();
		try {
			actorOf = context.actorOf(Props.create(UserActor.class, user, false, sender), userid);
			user.setActorRef(actorOf);
		} catch (Exception e) {
			createUserResult.result=false;
			byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.WORLD_CREATE_USER_RESULT, createUserResult);
			getSender().tell(actorMessageWithSubClass,sender);
		}
		createUserResult.result=true;
		userMap.put(userid, user);
		
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.WORLD_CREATE_USER_RESULT, createUserResult);
		worldRef.tell(actorMessageWithSubClass, sender);

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
				
				ReBindUser reBindUser=new ReBindUser(true);
				byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.SESSION_REBIND_USER, reBindUser);
//				new SessionActor.ReBindUser(true)
				getSender().tell(actorMessageWithSubClass, user.getActorRef());
			} else {
				ReBindUser reBindUser=new ReBindUser(false);
				byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.SESSION_REBIND_USER, reBindUser);
				getSender().tell(actorMessageWithSubClass, ActorRef.noSender());
			}
		} else {
			ReBindUser reBindUser=new ReBindUser(false);
			byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.SESSION_REBIND_USER, reBindUser);
			
			getSender().tell(actorMessageWithSubClass, ActorRef.noSender());
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

	private void removeUser(String userId) {
		userMap.remove(userId);
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

//	/**
//	 * 创建一个用户
//	 * 
//	 * @author xl
//	 *
//	 */
//	public static class CreateUser {
//		public final boolean npc;
//		public final String reBindKey;
//
//		public CreateUser(boolean npc, String reBindKey) {
//			super();
//			this.npc = npc;
//			this.reBindKey = reBindKey;
//		}
//	}

//	public static class CreateUserWithSession {
//		public final ActorRef ioSession;
//		public final String reBindKey;
//
//		public CreateUserWithSession(ActorRef ioSession, String reBindKey) {
//			super();
//			this.ioSession = ioSession;
//			this.reBindKey = reBindKey;
//		}
//
//	}

	public static class SetReBindKey {
		public final String reBindKey;
		public final String userId;

		public SetReBindKey(String reBindKey, String userId) {
			super();
			this.reBindKey = reBindKey;
			this.userId = userId;
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
