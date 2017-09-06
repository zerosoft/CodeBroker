package com.codebroker.core.manager;

import java.util.List;
import java.util.UUID;

import com.codebroker.api.IUser;
import com.codebroker.api.manager.IUserManager;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.usermanager.CreateUser;
import com.message.thrift.actor.usermanager.RemoveUser;

import akka.actor.ActorRef;

/**
 * 用户管理器API
 * 
 * @author ZERO
 *
 */
public class UserManager implements IUserManager {

	private ActorRef managerRef;

	@Override
	public IUser createUser(boolean npc) throws Exception {
		CreateUser createUser=new CreateUser(npc, UUID.randomUUID().toString());
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.USER_MANAGER_CREATE_USER, createUser);		
		
		return (IUser) AkkaMediator.getCallBak(managerRef,actorMessageWithSubClass);
	}

	@Override
	public void removeUser(String userId) {
		RemoveUser removeUser=new RemoveUser(userId);
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.USER_MANAGER_REMOVE_USER, removeUser);		
		
		managerRef.tell(actorMessageWithSubClass, ActorRef.noSender());
	}

	public ActorRef getManagerRef() {
		return managerRef;
	}

	public void setManagerRef(ActorRef managerRef) {
		this.managerRef = managerRef;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IUser> getPlayerUser() throws Exception {
		return (List<IUser>) AkkaMediator.getCallBak(managerRef,
				new UserManagerActor.GetUserList(UserManagerActor.GetUserList.Type.PLAYER));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IUser> getNPCUser() throws Exception {
		return (List<IUser>) AkkaMediator.getCallBak(managerRef,
				new UserManagerActor.GetUserList(UserManagerActor.GetUserList.Type.NPC));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IUser> getAllUser() throws Exception {
		return (List<IUser>) AkkaMediator.getCallBak(managerRef,
				new UserManagerActor.GetUserList(UserManagerActor.GetUserList.Type.ALL));
	}

	@Override
	public IUser getPlayerUser(String userId) throws Exception {
		return (IUser) AkkaMediator.getCallBak(managerRef, new UserManagerActor.GetPlayerUser(userId));
	}

}
