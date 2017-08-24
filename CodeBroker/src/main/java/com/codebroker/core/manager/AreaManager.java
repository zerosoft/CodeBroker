package com.codebroker.core.manager;

import java.util.Collection;

import com.codebroker.api.IArea;
import com.codebroker.api.manager.IAreaManager;
import com.codebroker.core.actor.AreaManagerActor;
import com.codebroker.util.AkkaMediator;

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
		IArea callBak = (IArea) AkkaMediator.getCallBak(gridLeaderRef, new AreaManagerActor.CreateArea(loaclAreaId));
		return callBak;
	}

	@Override
	public void removeArea(int loaclGridId) {
		gridLeaderRef.tell(new AreaManagerActor.RemoveArea(loaclGridId), ActorRef.noSender());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IArea> getAllArea() throws Exception {
		Collection<IArea> callBak = (Collection<IArea>) AkkaMediator.getCallBak(gridLeaderRef,
				new AreaManagerActor.GetAllArea());
		return callBak;
	}

	@Override
	public IArea getAreaById(String gridId) throws Exception {
		IArea callBak = (IArea) AkkaMediator.getCallBak(gridLeaderRef, new AreaManagerActor.GetAreaById(gridId));
		return callBak;
	}

}
