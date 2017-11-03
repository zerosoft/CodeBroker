package com.codebroker.core.entities;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import com.codebroker.api.IGrid;
import com.codebroker.api.IUser;
import com.codebroker.api.event.Event;
import com.codebroker.core.actor.GridActor;
import com.codebroker.exception.CodeBrokerException;
import com.codebroker.exception.NoActorRefException;
import com.codebroker.util.AkkaUtil;

import java.util.Collection;
import java.util.List;

/**
 * 区域下级的格子
 *
 * @author xl
 */
public class Grid implements IGrid {
    private ActorRef actorRef;

    public ActorRef getActorRef() {
        if (actorRef == null) {
            throw new NoActorRefException();
        }
        return actorRef;
    }

    @Override
    public boolean enterGrid(IUser user) throws Exception {
        return (boolean) AkkaUtil.getCallBak(getActorRef(), new GridActor.EnterGrid(user));
    }

    @Override
    public void leaveGrid(String userID) {
        getActorRef().tell(new GridActor.LeaveGrid(userID), ActorRef.noSender());
    }

    public void destory() {
        getActorRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    public void destroy() {
        getActorRef().tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    public void broadCastAllUser(Event object) {
        getActorRef().tell(object, ActorRef.noSender());
    }

    @Override
    public void broadCastUsers(Event object, Collection<IUser> users) {
        for (IUser iUser : users) {
//            iUser.dispatchEvent(object);
        }
    }

    @Override
    public String getId() throws Exception {
        if (getActorRef() != null) {
            return getActorRef().path().name();
        }
        throw new CodeBrokerException("NO");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IUser> getPlayers() throws Exception {
        return (List<IUser>) AkkaUtil.getCallBak(getActorRef(), new GridActor.GetPlayers());
    }

}
