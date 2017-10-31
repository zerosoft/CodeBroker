package com.codebroker.core.actor;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.codebroker.api.event.Event;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.Grid;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.core.message.ScheduleTask;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.CreateGrid;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.RemoveGrid;
import com.message.thrift.actor.area.UserEneterArea;
import org.apache.thrift.TException;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 区域 管理区域中的格子
 *
 * @author zero
 */
public class AreaActor extends AbstractActor {

    public transient static final String AREA_CLOSE = "AREA_CLOSE";
    public transient static final String AREA_ENTER_USER = "AREA_ENTER_USER";
    public transient static final String AREA_LEAVE_USER = "AREA_LEAVE_USER";
    public transient static final String USER_ID = "USER_ID";
    private final String areaId;
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    // 用户
//    private Map<String, ActorRef> userMap = new TreeMap<String, ActorRef>();
    // 格子
    private Map<String, Grid> gridMap = new TreeMap<String, Grid>();

    public AreaActor(String areaId) {
        this.areaId = areaId;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        Iterable<ActorRef> children = getContext().getChildren();

        for (ActorRef childRef : children) {
            childRef.tell(PoisonPill.getInstance(), getSelf());
        }

        for (ActorRef iUser : userMap.values()) {
            Event event = new Event();
            event.setTopic(AREA_CLOSE);
            iUser.tell(event, getSelf());
        }
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(byte[].class, msg -> {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        case AREA_USER_ENTER_AREA:
                            UserEneterArea eneterArea = new UserEneterArea();
                            thriftSerializerFactory.deserialize(eneterArea, actorMessage.messageRaw);
                            enterArea(eneterArea.userId, getSender());
                            break;
                        case AREA_USER_LEAVE_AREA:
                            LeaveArea leaveArea = new LeaveArea();
                            thriftSerializerFactory.deserialize(leaveArea, actorMessage.messageRaw);
                            leaveArea(leaveArea.userId);
                            break;
                        case AREA_CREATE_GRID:
                            CreateGrid createGrid = new CreateGrid();
                            thriftSerializerFactory.deserialize(createGrid, actorMessage.messageRaw);
                            createGrid(createGrid.getGridId());
                            break;
                        case AREA_REMOVE_GRID:
                            RemoveGrid removeGrid = new RemoveGrid();
                            thriftSerializerFactory.deserialize(removeGrid, actorMessage.messageRaw);
                            removeGrid(removeGrid.getGridId());
                        default:
                            break;
                    }
                })
                .match(ScheduleTask.class, msg -> {
                    Scheduler scheduler = getContext().getSystem().scheduler();
                    if (msg.isOnce()) {
                        Cancellable cancellable = scheduler.scheduleOnce(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    } else {
                        Cancellable schedule = scheduler.schedule(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), Duration.create(msg.getInterval(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    }
                })
                .match(GetGridById.class, msg -> {
                    getGridById(msg);
                }).match(GetAllGrids.class, msg -> {
                    getAllGrid();
                })
                .match(Terminated.class, msg -> {
                    String name = msg.actor().path().name();
                })
                .build();
    }


    private void broadCastAllUser(Event object) {
        Collection<ActorRef> values = userMap.values();
        for (ActorRef iUser : values) {
            iUser.tell(object, getSelf());
        }
    }

    private void createGrid(String gridId) {
        if (gridMap.containsKey(gridId)) {
            getSender().tell(gridMap.get(gridId), getSelf());
        } else {

            Grid gridProxy = new Grid();
            ActorRef actorOf = getContext().actorOf(Props.create(GridActor.class, getSelf()), gridId);
            gridProxy.setActorRef(actorOf);

            getContext().watch(actorOf);
            getSender().tell(gridProxy, getSelf());

            gridMap.put(gridId, gridProxy);

            ServerEngine.envelope.subscribe(actorOf, getSelf().path().name());
        }
    }

    private void removeGrid(String gridId) {
        Grid grid2 = gridMap.get(gridId);
        if (grid2 != null) {
            ServerEngine.envelope.unsubscribe(grid2.getActorRef());
            grid2.destory();
        }
    }

    private void getAllGrid() {
        Collection<Grid> values = gridMap.values();
        List<Grid> list = new ArrayList<Grid>();
        list.addAll(values);
        getSender().tell(list, getSelf());
    }

    private void getGridById(GetGridById msg) {
        Grid grid2 = gridMap.get(msg.gridId);
        if (grid2 != null) {
            getSender().tell(grid2, getSelf());
        }
    }

    private void leaveArea(String userId) {
        if (userMap.containsKey(userId)) {
            userMap.remove(userId);

            Event event = new Event();
            event.setTopic(AREA_LEAVE_USER);
            IObject iObject = CObject.newInstance();
            iObject.putUtfString(USER_ID, userId);
            event.setMessage(iObject);

            // 广播玩家离开
            broadCastAllUser(event);
        }
    }

    /**
     * 进入区域
     *
     * @param userId
     * @param sender
     */
    private void enterArea(String userId, ActorRef sender) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        Map<String, String> areaUserActorRefPath = component.getAreaUserActorRefPath(areaId);
        if (areaUserActorRefPath.containsKey(userId)) {
            getSender().tell(false, getSelf());
        } else {
            component.setAreaUserRefPath(areaId, userId, sender);
//            userMap.put(userId, sender);
            getSender().tell(true, getSelf());
            // 通知user进入所在actor

            byte[] tbaseMessage;
            try {
                tbaseMessage = thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_ENTER_AREA);
                sender.tell(tbaseMessage, getSelf());

            } catch (TException e) {
                e.printStackTrace();
            }

            Event event = new Event();
            event.setTopic(AREA_ENTER_USER);

            IObject object = CObject.newInstance();
            object.putUtfString(USER_ID, userId);
            event.setMessage(object);

            broadCastAllUser(event);
        }
    }


    public static class GetGridById implements Serializable {
        private static final long serialVersionUID = -4927817351189923926L;
        public final String gridId;

        public GetGridById(String gridId) {
            super();
            this.gridId = gridId;
        }

    }

    public static class GetAllGrids implements Serializable {

        private static final long serialVersionUID = -1778022483881100165L;

    }


    public static class GetPlayers implements Serializable {

        private static final long serialVersionUID = -1823532488763688181L;

    }
}
