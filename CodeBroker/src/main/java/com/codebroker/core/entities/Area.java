package com.codebroker.core.entities;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import com.codebroker.api.IArea;
import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.Event;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.area.CreateGrid;
import com.message.thrift.actor.area.LeaveArea;
import com.message.thrift.actor.area.RemoveGrid;
import com.message.thrift.actor.area.UserEneterArea;

import java.util.Collection;
import java.util.List;

/**
 * 区域 区域下可以有N多个Grid
 *
 * @author xl
 */
public class Area implements IArea {

    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();

    private ActorRef actorRef;

    public Area(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    @Override
    public void enterArea(IUser user) {
        UserEneterArea eneterArea = new UserEneterArea(user.getUserId());
        actorRef.tell(
                thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_USER_ENTER_AREA, eneterArea),
                actorRef);
    }

    @Override
    public String getId() {
        if (actorRef != null) {
            return actorRef.path().name();
        }
        throw new CodeBrokerException("NO");
    }


    @Override
    public void leaveArea(String userID) {
        LeaveArea base = new LeaveArea(userID);
        actorRef.tell(
                thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_USER_LEAVE_AREA, base),
                ActorRef.noSender());
    }

    @Override
    public void createGrid(String gridId) {
        CreateGrid base = new CreateGrid(gridId);
        actorRef.tell(thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_CREATE_GRID, base), ActorRef.noSender());
    }

    @Override
    public void removeGridById(String gridId) {
        RemoveGrid base = new RemoveGrid(gridId);
        actorRef.tell(thriftSerializerFactory.getActorMessageByteArray(Operation.AREA_REMOVE_GRID, base), ActorRef.noSender());
    }

    @Override
    public IGrid getGridById(String gridId) {
        return null;
        //        return (IGrid) AkkaUtil.getCallBak(getActorRef(), new AreaActor.GetGridById(gridId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<IGrid> getAllGrid() {
        return null;
//        return (Collection<IGrid>) AkkaUtil.getCallBak(getActorRef(), new AreaActor.GetAllGrids());
    }

    @Override
    public void broadCastAllUser(Event object) {
        actorRef.tell(object, ActorRef.noSender());
    }

    @Override
    public void broadCastUsers(Event object, Collection<IUser> users) {
        for (IUser iUser : users) {
//            iUser.dispatchEvent(object);
        }
    }

    @Override
    public void destroy() {
        actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IUser> getPlayers() {
        return null;
//        return (List<IUser>) AkkaUtil.getCallBak(getActorRef(), new AreaActor.GetPlayers());
    }

}
