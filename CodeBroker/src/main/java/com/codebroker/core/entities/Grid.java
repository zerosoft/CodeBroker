package com.codebroker.core.entities;

import java.util.Collection;

import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
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
public class Grid implements IGrid {

	private ActorRef gridRef;

	public void setGridRef(ActorRef gridRef) {
		this.gridRef = gridRef;
	}

	public ActorRef getGridRef() {
		return gridRef;
	}

	@Override
	public boolean enterGrid(IUser user) throws Exception {
		return (boolean) AkkaMediator.getCallBak(gridRef, new GridActor.EnterGrid(user));
	}

	@Override
	public void leaveGrid(String userID) {
		gridRef.tell(new GridActor.LeaveGrid(userID), ActorRef.noSender());
	}

	public void destory() {
		gridRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@Override
	public void destroy() {
		gridRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@Override
	public void broadCastAllUser(String jsonString) {
		gridRef.tell(new GridActor.BroadCastAllUser(jsonString), ActorRef.noSender());
	}

	@Override
	public void broadCastUsers(String jsonString, Collection<IUser> users) {
		for (IUser iUser : users) {
			CodeEvent codeEvent=new CodeEvent();
			codeEvent.setTopic(CodeBrokerEvent.GRID_EVENT);
			codeEvent.setParameter(jsonString);
			iUser.dispatchEvent(codeEvent);
		}
	}

	@Override
	public String getId() throws Exception {
		if (gridRef != null) {
			return gridRef.path().name();
		}
		throw new CodeBrokerException("NO");
	}

	@Override
	public void addEventListener(String topic, IEventListener eventListener) {
		gridRef.tell(new GridActor.AddEventListener(topic,eventListener), ActorRef.noSender());
	}

	@Override
	public boolean hasEventListener(String paramString) {
		try {
			return AkkaMediator.getCallBak(gridRef, new GridActor.HasEventListener(paramString));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void removeEventListener(String paramString) {
			gridRef.tell(new GridActor.RemoveEventListener(paramString), ActorRef.noSender());
		
	}

	@Override
	public void dispatchEvent(IEvent paramIEvent) {
			gridRef.tell(new GridActor.DispatchEvent(paramIEvent), ActorRef.noSender());
	}

}
