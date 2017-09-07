package com.codebroker.core.actor;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.codebroker.core.entities.User;
import com.codebroker.core.local.WorldCreateNPC;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.session.UserConnect2Server;
import com.message.thrift.actor.usermanager.RemoveUser;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class NPCManagerActor extends AbstractActor {


	public static final String IDENTIFY = NPCManagerActor.class.getSimpleName().toString();

	private static final AtomicInteger USER_ID = new AtomicInteger(1);
	
	private Map<String, ActorRef> userRefMap = new TreeMap<String, ActorRef>();
	
	private final ActorRef worldRef;

	public NPCManagerActor(ActorRef worldRef) {
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
			//创建NPC
			case USER_MANAGER_CREATE_USER:
//				CreateUser createUser=new CreateUser();
//				ThriftSerializerFactory.deserialize(createUser, actorMessage.messageRaw);
//				createUser(createUser.npc, null, createUser.reBindKey);
				break;
			//移除玩家
			case USER_MANAGER_REMOVE_USER:
				RemoveUser removeUser=new RemoveUser();
				ThriftSerializerFactory.deserialize(removeUser, actorMessage.messageRaw);
				removeUser(removeUser.userID);
				break;
			default:

				break;
			}
		})
		.match(WorldCreateNPC.class, msg->{
			
		})
		.build();
	}




	private void removeUser(String userId) {
		userRefMap.remove(userId);
	}

	private void createUser(boolean npc, ActorRef ioSession, String reBindKey) {
		int id = USER_ID.incrementAndGet();
		User user = new User();
		ActorContext context = getContext();

		String userid = WorldActor.USER_PRFIX + id;
		user.setUserId(userid);

		ActorRef actorOf = context.actorOf(Props.create(UserActor.class, user, npc, ioSession), userid);
		user.setActorRef(actorOf);
		//放入
		userRefMap.put(userid, actorOf);
		
		ioSession.tell(ThriftSerializerFactory.getActorMessageWithSubClass(
				Operation.SESSION_USER_CONNECT_TO_SERVER, new UserConnect2Server(true)),actorOf);
	}





	public static class FindUserByRebindKey {
		public final String bindKey;

		public FindUserByRebindKey(String bindKey) {
			super();
			this.bindKey = bindKey;
		}

	}
}
