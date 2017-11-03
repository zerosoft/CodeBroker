package com.codebroker.core.actor;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import akka.serialization.Serialization;
import com.codebroker.api.event.Event;
import com.codebroker.cache.AreaInfoCache;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.ServerEngine;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import com.codebroker.core.entities.Grid;
import com.codebroker.core.manager.CacheManager;
import com.codebroker.core.message.ScheduleTask;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.LogUtil;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.CreateGrid;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.RemoveGrid;
import com.message.thrift.actor.area.UserEneterArea;
import com.message.thrift.actor.user.UserLeaveArea;
import org.apache.thrift.TException;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    private List<Cancellable> runTask=new ArrayList<Cancellable>();

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
        for (Cancellable cancellable : runTask) {
            if (!cancellable.isCancelled())
                cancellable.cancel();
        }

        Event event = new Event();
        event.setTopic(AREA_CLOSE);
        broadCastAllUser(event);
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
                            break;
                        default:
                            break;
                    }
                })
                .match(ScheduleTask.class, msg -> {
                    Scheduler scheduler = getContext().getSystem().scheduler();
                    Cancellable cancellable;
                    if (msg.isOnce()) {
                         cancellable = scheduler.scheduleOnce(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    } else {
                        cancellable = scheduler.schedule(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), Duration.create(msg.getInterval(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    }
                    runTask.add(cancellable);
                })
                .match(Terminated.class, msg -> {
                    String name = msg.actor().path().name();
                })
                .build();
    }


    private void broadCastAllUser(Event object) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        AreaInfoCache areaInfoCache = component.getAreaInfoCache(areaId);

        Map<String, String> userPath = areaInfoCache.getUserPath();
        for (Map.Entry<String, String> stringStringEntry : userPath.entrySet()) {
            String value = stringStringEntry.getValue();
            ActorRef actorRef = ContextResolver.getActorSystem().provider().resolveActorRef(value);
            actorRef.tell(object, getSelf());
        }

    }

    /**
     * 创建一个格子
     * @param gridId
     */
    private void createGrid(String gridId) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        AreaInfoCache areaInfoCache = component.getAreaInfoCache(areaId);
        Map<String, String> gridPath = areaInfoCache.getGridPath();
        if (!gridPath.containsKey(gridId)) {
            ActorRef actorOf = getContext().actorOf(Props.create(GridActor.class, getSelf()), gridId);
            getContext().watch(actorOf);

            gridPath.put(gridId,Serialization.serializedActorPath(actorOf));
            component.putAreaInfoCache(areaId,areaInfoCache);
        }
    }

    /**
     * 移除一个格子
     * @param gridId
     */
    private void removeGrid(String gridId) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        AreaInfoCache areaInfoCache = component.getAreaInfoCache(areaId);
        final Map<String, String> gridPath = areaInfoCache.getGridPath();
        if (gridPath.containsKey(gridId)){
            gridPath.remove(gridId);
            component.putAreaInfoCache(areaId,areaInfoCache);
        }
    }


    private void leaveArea(String userId) {
        CacheManager component = ContextResolver.getComponent(CacheManager.class);
        AreaInfoCache areaInfoCache = component.getAreaInfoCache(areaId);
        final Map<String, String> userPath = areaInfoCache.getUserPath();
        UserLeaveArea leaveArea=new UserLeaveArea();
        if (userPath.containsKey(userId)) {
            leaveArea.setUserId(userId);
            leaveArea.setUserIdIsSet(true);


            byte[]   tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_LEAVE_AREA,leaveArea);
            getSender().tell(tbaseMessage, getSelf());

            component.removeGlobalActorRefPath(userId);
            Event event = new Event();
            event.setTopic(AREA_LEAVE_USER);
            IObject iObject = CObject.newInstance();
            iObject.putUtfString(USER_ID, userId);
            event.setMessage(iObject);

            // 广播玩家离开
            broadCastAllUser(event);
        }else{
            leaveArea.setUserId(userId);
            leaveArea.setUserIdIsSet(false);

            byte[]   tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_LEAVE_AREA,leaveArea);
            getSender().tell(tbaseMessage, getSelf());
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

        AreaInfoCache areaInfoCache = component.getAreaInfoCache(areaId);
        Map<String, String> userPath = areaInfoCache.getUserPath();
        UserEneterArea userEneterArea=new UserEneterArea();
        if (userPath.containsKey(userId)) {

            userEneterArea.setUserId(userId);
            userEneterArea.setUserIdIsSet(false);
            byte[]   tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_ENTER_AREA,userEneterArea);
            sender.tell(tbaseMessage, getSelf());
        } else {
            userPath.put(userId, Serialization.serializedActorPath(sender));
            component.putAreaInfoCache(areaId,areaInfoCache);
            // 通知user进入所在actor
            userEneterArea.setUserId(userId);
            userEneterArea.setUserIdIsSet(true);
            byte[]   tbaseMessage = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_ENTER_AREA,userEneterArea);
            sender.tell(tbaseMessage, getSelf());

            Event event = new Event();
            event.setTopic(AREA_ENTER_USER);

            IObject object = CObject.newInstance();
            object.putUtfString(USER_ID, userId);
            event.setMessage(object);

            broadCastAllUser(event);
        }
    }


}
