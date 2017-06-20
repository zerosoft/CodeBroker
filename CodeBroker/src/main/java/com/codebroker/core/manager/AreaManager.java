package com.codebroker.core.manager;

import java.util.Collection;

import com.codebroker.api.IArea;
import com.codebroker.api.manager.IAreaManager;
import com.codebroker.core.actor.AreaManagerActor;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * 区域管理器
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
	public IArea createGrid(int loaclGridId) throws Exception {
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(gridLeaderRef, new AreaManagerActor.CreateArea(loaclGridId), timeout);
		IArea result = (IArea) Await.result(future, timeout.duration());
		return result;
	}

	@Override
	public void removeGrid(int loaclGridId) {
		gridLeaderRef.tell(new AreaManagerActor.RemoveArea(loaclGridId), ActorRef.noSender());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IArea> getAllGrid() throws Exception {
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(gridLeaderRef, new AreaManagerActor.GetAllArea(), timeout);
		Collection<IArea> result = (Collection<IArea>) Await.result(future, timeout.duration());
		return result;
	}

}
