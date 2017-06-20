package com.codebroker.core.entities;

import java.io.Serializable;
import java.util.Collection;

import com.codebroker.api.IArea;
import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.IEvent;
import com.codebroker.api.event.IEventListener;
import com.codebroker.core.actor.AreaActor;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.util.AkkaMediator;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;

/**
 * 区域 区域下可以有N多个Grid
 * 
 * @author xl
 *
 */
public class Area implements IArea, Serializable {

	private static final long serialVersionUID = -1706402463773889231L;

	private ActorRef areaRef;

	public void setAreaRef(ActorRef gridRef) {
		this.areaRef = gridRef;
	}

	public ActorRef getAreaRef() {
		return areaRef;
	}

	@Override
	public boolean enterArea(IUser user) throws Exception {
		return (boolean) AkkaMediator.getCallBak(areaRef, new AreaActor.EnterArea(user));
	}

	@Override
	public String getId() throws Exception {
		if (areaRef != null) {
			return areaRef.path().name();
		}
		throw new CodeBrokerException("NO");
	}

	@Override
	public IUser createNPC() throws Exception {
		return (IUser) AkkaMediator.getCallBak(areaRef, new AreaActor.CreateNPC());
	}

	@Override
	public void leaveArea(String userID) {
		areaRef.tell(new AreaActor.LeaveArea(userID), ActorRef.noSender());
	}

	@Override
	public void removeNPC(String npcId) {
		areaRef.tell(new AreaActor.LeaveArea(npcId), ActorRef.noSender());
	}

	@Override
	public IGrid createGrid(String gridId) throws Exception {
		return (IGrid) AkkaMediator.getCallBak(areaRef, new AreaActor.CreateGrid(gridId));
	}

	@Override
	public void removeGridById(String gridId) {
		areaRef.tell(new AreaActor.RemoveGrid(gridId), ActorRef.noSender());
	}

	@Override
	public IGrid getGridById(String gridId) throws Exception {
		return (IGrid) AkkaMediator.getCallBak(areaRef, new AreaActor.GetGridById(gridId));
	}

	@Override
	public Collection<IGrid> getAllGrid() throws Exception {
		return (Collection<IGrid>) AkkaMediator.getCallBak(areaRef, new AreaActor.GetAllGrids());
	}

	@Override
	public void broadCastAllUser(String jsonString) {
		areaRef.tell(new AreaActor.BroadCastAllUser(jsonString), ActorRef.noSender());
	}

	@Override
	public void broadCastUsers(String jsonString, Collection<IUser> users) {
		for (IUser iUser : users) {
			CodeEvent codeEvent = new CodeEvent();
			codeEvent.setTopic(CodeBrokerEvent.AREA_EVENT);
			codeEvent.setParameter(jsonString);
			iUser.dispatchEvent(codeEvent);
		}
	}

	@Override
	public void destroy() {
		areaRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@Override
	public void addEventListener(String topic, IEventListener eventListener) {
		areaRef.tell(new AreaActor.AddEventListener(topic, eventListener), ActorRef.noSender());
	}

	@Override
	public boolean hasEventListener(String paramString) {
		try {
			return AkkaMediator.getCallBak(areaRef, new AreaActor.HasEventListener(paramString));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void removeEventListener(String paramString) {
		areaRef.tell(new AreaActor.RemoveEventListener(paramString), ActorRef.noSender());
	}

	@Override
	public void dispatchEvent(IEvent paramIEvent) {
		areaRef.tell(new AreaActor.DispatchEvent(paramIEvent), ActorRef.noSender());
	}

}
