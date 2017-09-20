package com.codebroker.core.entities;

import java.util.Collection;
import java.util.List;

import com.codebroker.api.IArea;
import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.EventTypes;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.actor.AreaActor;
import com.codebroker.core.data.IObject;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.LeaveArea;
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
		LeaveArea leaveArea = new LeaveArea(userID);
		getActorRef().tell(
				thriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_USER_LEAVE_AREA, leaveArea),
				ActorRef.noSender());
	}

	@Override
	public IGrid createGrid(String gridId) throws Exception {
		return (IGrid) AkkaMediator.getCallBak(getActorRef(), new AreaActor.CreateGrid(gridId));
	}

	@Override
	public void removeGridById(String gridId) {
		getActorRef().tell(new AreaActor.RemoveGrid(gridId), ActorRef.noSender());
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
	public void broadCastAllUser(IObject object) {
		object.putInt(EventTypes.KEY, EventTypes.AREA_BROAD_CAST);
		getActorRef().tell(object, ActorRef.noSender());
	}

	@Override
	public void broadCastUsers(IObject object, Collection<IUser> users) {
		object.putInt(EventTypes.KEY, EventTypes.AREA_BROAD_CAST);
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
