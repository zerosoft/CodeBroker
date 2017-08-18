package com.codebroker.core.manager;

import java.util.List;
import java.util.UUID;

import com.codebroker.api.IUser;
import com.codebroker.api.manager.IUserManager;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.util.AkkaMediator;

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
		return (IUser) AkkaMediator.getCallBak(managerRef,
				new UserManagerActor.CreateUser(npc, UUID.randomUUID().toString()));
	}

	@Override
	public void removeUser(String userId) {
		managerRef.tell(new UserManagerActor.RemoveUser(userId), ActorRef.noSender());
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
