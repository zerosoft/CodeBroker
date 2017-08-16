package com.codebroker.core.entities;

import java.util.Collection;
import java.util.List;

import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.actor.GridActor;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.util.AkkaMediator;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;

/**
 * 区域下级的格子
 * 
 * @author xl
 *
 */
public class Grid extends EventDispatcher implements IGrid {

	@Override
	public boolean enterGrid(IUser user) throws Exception {
		return (boolean) AkkaMediator.getCallBak(getActorRef(), new GridActor.EnterGrid(user));
	}

	@Override
	public void leaveGrid(String userID) {
		getActorRef().tell(new GridActor.LeaveGrid(userID), ActorRef.noSender());
	}

	public void destory() {
		getActorRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@Override
	public void destroy() {
		getActorRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@Override
	public void broadCastAllUser(String jsonString) {
		getActorRef().tell(new GridActor.BroadCastAllUser(jsonString), ActorRef.noSender());
	}

	@Override
	public void broadCastUsers(String jsonString, Collection<IUser> users) {
		for (IUser iUser : users) {
			CodeEvent codeEvent = new CodeEvent();
			codeEvent.setTopic(CodeBrokerEvent.GRID_EVENT);
			codeEvent.setParameter(jsonString);
			iUser.dispatchEvent(codeEvent);
		}
	}

	@Override
	public String getId() throws Exception {
		if (getActorRef() != null) {
			return getActorRef().path().name();
		}
		throw new CodeBrokerException("NO");
	}

	@Override
	public List<IUser> getPlayers() throws Exception {
		return (List<IUser>) AkkaMediator.getCallBak(getActorRef(), new GridActor.GetPlayers());
	}

}
