package com.codebroker.core.entities;

import java.util.Collection;
import java.util.List;

import com.codebroker.api.IArea;
import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.Event;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.actor.AreaActor;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.CreateGrid;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.RemoveGrid;
import com.message.thrift.actor.area.UserEneterArea;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;

/**
 * 区域 区域下可以有N多个Grid
 * 
 * @author xl
 *
 */
public class Area extends EventDispatcher implements IArea {
	ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
	@Override
	public void enterArea(IUser user) throws Exception {
		UserEneterArea eneterArea = new UserEneterArea(user.getUserId());
		getActorRef().tell(
				thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_USER_ENTER_AREA, eneterArea),
				getActorRef());
	}

	@Override
	public String getId() throws Exception {
		if (getActorRef() != null) {
			return getActorRef().path().name();
		}
		throw new CodeBrokerException("NO");
	}


	@Override
	public void leaveArea(String userID) {
		LeaveArea base = new LeaveArea(userID);
		getActorRef().tell(
				thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_USER_LEAVE_AREA, base),
				ActorRef.noSender());
	}

	@Override
	public void createGrid(String gridId) throws Exception {
		CreateGrid base=new CreateGrid(gridId);
		getActorRef().tell(thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_CREATE_GRID, base), ActorRef.noSender());
	}

	@Override
	public void removeGridById(String gridId) {
		RemoveGrid base = new RemoveGrid(gridId);
		getActorRef().tell(thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_REMOVE_GRID, base), ActorRef.noSender());
	}

	@Override
	public IGrid getGridById(String gridId) throws Exception {
		return (IGrid) AkkaMediator.getCallBak(getActorRef(), new AreaActor.GetGridById(gridId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IGrid> getAllGrid() throws Exception {
		return (Collection<IGrid>) AkkaMediator.getCallBak(getActorRef(), new AreaActor.GetAllGrids());
	}

	@Override
	public void broadCastAllUser(Event object) {
		getActorRef().tell(object, ActorRef.noSender());
	}

	@Override
	public void broadCastUsers(Event object, Collection<IUser> users) {
		for (IUser iUser : users) {
			iUser.dispatchEvent(object);
		}
	}

	@Override
	public void destroy() {
		getActorRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IUser> getPlayers() throws Exception {
		return (List<IUser>) AkkaMediator.getCallBak(getActorRef(), new AreaActor.GetPlayers());
	}

}
