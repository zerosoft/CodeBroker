package com.codebroker.core.manager;

import java.util.Collection;

import com.codebroker.api.IArea;
import com.codebroker.api.manager.IAreaManager;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.areamanager.CreateArea;
import com.message.thrift.actor.areamanager.GetAreaById;
import com.message.thrift.actor.areamanager.RemoveArea;

import akka.actor.ActorRef;

/**
 * 区域管理器
 * 
 * @author ZERO
 *
 */
public class AreaManager implements IAreaManager {

	private final ActorRef gridLeaderRef;

	public AreaManager(ActorRef gridLeaderRef) {
		super();
		this.gridLeaderRef = gridLeaderRef;
	}

	@Override
	public IArea createArea(int loaclAreaId) throws Exception {
		CreateArea message=new CreateArea(loaclAreaId);
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_MANAGER_CREATE_AREA, message);
		IArea callBak = (IArea) AkkaMediator.getCallBak(gridLeaderRef,actorMessageWithSubClass);
		return callBak;
	}

	@Override
	public void removeArea(int loaclAreaId) {
		RemoveArea message=new RemoveArea(loaclAreaId);
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_MANAGER_REMOVE_AREA, message);
		gridLeaderRef.tell(actorMessageWithSubClass, ActorRef.noSender());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IArea> getAllArea() throws Exception {
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getTbaseMessage(Operation.AREA_MANAGER_GET_ALL_AREA);
		Collection<IArea> callBak = (Collection<IArea>) AkkaMediator.getCallBak(gridLeaderRef,actorMessageWithSubClass);
		return callBak;
	}

	@Override
	public IArea getAreaById(String areaId) throws Exception {
		GetAreaById message=new GetAreaById(areaId);
		byte[] actorMessageWithSubClass = ThriftSerializerFactory.getActorMessageWithSubClass(Operation.AREA_MANAGER_GET_AREA_BY_ID, message);
		IArea callBak = (IArea) AkkaMediator.getCallBak(gridLeaderRef, actorMessageWithSubClass);
		return callBak;
	}

}
