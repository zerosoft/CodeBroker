package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.codebroker.core.ServerEngine;
import com.codebroker.core.cluster.ClusterDistributedSub.Subscribe;
import com.codebroker.core.entities.Area;
import com.codebroker.util.AkkaMediator;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

public class AreaManagerActor extends AbstractActor {

	public static final String IDENTIFY = AreaManagerActor.class.getSimpleName();

	private Map<String, Area> gridMap = new TreeMap<String, Area>();

	ActorRef mediator;

	@Override
	public void preStart() throws Exception {
		super.preStart();
		mediator = DistributedPubSub.get(getContext().system()).mediator();
		mediator.tell(new DistributedPubSubMediator.Subscribe(IDENTIFY, getSelf()), getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
		 .match(CreateArea.class, msg -> {
			createGrid(msg.loaclGridId);
		}).match(RemoveArea.class, msg -> {
			removeGridById(msg.loaclGridId);
		}).match(GetAllArea.class, msg -> {
			getAllArea();
		}).match(NewServerComeIn.class, msg -> {
			newServerComeIn(msg.serverId,msg.fixSupervisorPath);
		}).match(GiveMeYourArea.class, msg -> {
			giveMeYourArea();
		}).match(GiveYourMyAreas.class, msg -> {
			giveYourMyAreas(msg.areas);
		}).match(Subscribe.class, msg -> {
			mediator.tell(new DistributedPubSubMediator.Subscribe(IDENTIFY, getSelf()), getSelf());
		}).match(AddArea.class, msg -> {
			addArea(msg.serverId,msg.area);
		}).match(DelArea.class, msg -> {
			delArea(msg.serverId,msg.areaId);
		}).matchAny(msg -> {
			System.out.println(msg);
		}).build();
	}

	private void delArea(int serverId, String areaId) {
		if (ServerEngine.serverId != serverId) {
			gridMap.remove(areaId);
		}
	}

	private void addArea(int serverId, Area area) throws Exception {
		if (ServerEngine.serverId != serverId) {
			gridMap.put(area.getId(), area);
		}
	}

	private void createGrid(int loaclGridId) {
		String key = ServerEngine.serverId + "_" + loaclGridId;
		if (gridMap.containsKey(key)) {
			getSender().tell(gridMap.get(loaclGridId), getSelf());
		} else {
			Area gridProxy = new Area();
			ActorRef actorOf = getContext().actorOf(Props.create(AreaActor.class, gridProxy), key);

			gridProxy.setAreaRef(actorOf);

			getContext().watch(actorOf);
			getSender().tell(gridProxy, getSelf());

			gridMap.put(key, gridProxy);
			// 发送到通道
			mediator.tell(new DistributedPubSubMediator.Publish(IDENTIFY,
					new AreaManagerActor.AddArea(ServerEngine.serverId, gridProxy)), getSelf());
		}
	}

	private void removeGridById(int loaclGridId) {
		String key = ServerEngine.serverId + "_" + loaclGridId;
		Area gridProxy = gridMap.get(key);
		getContext().stop(gridProxy.getAreaRef());
		gridMap.remove(key);
		// 发送到通道
		mediator.tell(new DistributedPubSubMediator.Publish(IDENTIFY,
				new AreaManagerActor.DelArea(ServerEngine.serverId, key)), getSelf());
	}

	private void giveYourMyAreas(Collection<Area> areas) throws Exception {
		for (Area area : areas) {
			gridMap.put(area.getId(), area);
		}
	}

	private void giveMeYourArea() {
		Collection<Area> values = gridMap.values();
		List<Area> list = new ArrayList<Area>();
		list.addAll(values);
		getSender().tell(new GiveYourMyAreas(list), getSelf());
	}

	private void getAllArea() {
		Collection<Area> values = gridMap.values();
		List<Area> list = new ArrayList<Area>();
		list.addAll(values);
		getSender().tell(list, getSelf());
	}

	private void newServerComeIn(int serverId, String fixSupervisorPath) {
		// akka.tcp://CodeBroker@192.168.0.202:2551/user/WorldActor/AreaManagerActor
		String remotePath = fixSupervisorPath + "/" + AreaManagerActor.IDENTIFY;
		ActorSelection remoteActorSelection = AkkaMediator.getRemoteActorSelection(remotePath);
		remoteActorSelection.tell(new GiveMeYourArea(), getSelf());
	}

	public static class CreateArea implements Serializable {

		private static final long serialVersionUID = -1232160366248486176L;

		public final int loaclGridId;

		public CreateArea(int loaclGridId) {
			super();
			this.loaclGridId = loaclGridId;
		}
	}

	public static class RemoveArea implements Serializable {
		private static final long serialVersionUID = -402901433286253092L;
		public final int loaclGridId;

		public RemoveArea(int loaclGridId) {
			super();
			this.loaclGridId = loaclGridId;
		}
	}

	public static class GetAllArea implements Serializable {
		private static final long serialVersionUID = 4604002932157201267L;
	}

	public static class GiveYourMyAreas implements Serializable {

		private static final long serialVersionUID = 5710290268726529358L;

		public final Collection<Area> areas;

		public GiveYourMyAreas(Collection<Area> areas) {
			super();
			this.areas = areas;
		}

	}

	public static class AddArea implements Serializable {

		private static final long serialVersionUID = 5536989985398902217L;

		public final int serverId;
		public final Area area;

		public AddArea(int serverId, Area area) {
			super();
			this.serverId = serverId;
			this.area = area;
		}

	}

	public static class DelArea implements Serializable {

		private static final long serialVersionUID = 4051984914024834418L;

		public final int serverId;
		public final String areaId;

		public DelArea(int serverId, String areaId) {
			super();
			this.serverId = serverId;
			this.areaId = areaId;
		}

	}

	public static class GiveMeYourArea implements Serializable {
		private static final long serialVersionUID = -8851146819867048538L;
	}

	public static class NewServerComeIn implements Serializable {
		private static final long serialVersionUID = 3476800642861020459L;
		public final int serverId;
		public final String fixSupervisorPath;

		public NewServerComeIn(int serverId, String fixSupervisorPath) {
			super();
			this.serverId = serverId;
			this.fixSupervisorPath = fixSupervisorPath;
		}

	}
}
