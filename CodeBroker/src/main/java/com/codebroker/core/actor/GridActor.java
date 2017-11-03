package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.codebroker.api.IUser;
import com.codebroker.api.event.Event;
import com.codebroker.core.message.ScheduleTask;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 格子Actor对象
 *
 * @author zero
 */
public class GridActor extends AbstractActor {
    /**
     * 父类Actor
     */
    private final ActorRef parentAreaRef;
    private Map<String, IUser> userMap = new TreeMap<String, IUser>();

    public GridActor(ActorRef parentAreaRef) {
        super();
        this.parentAreaRef = parentAreaRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(EnterGrid.class, msg -> {
                    if (userMap.containsKey(msg.user.getUserId())) {
                        getSender().tell(false, getSelf());
                    } else {
                        userMap.put(msg.user.getUserId(), msg.user);
                        getSender().tell(true, getSelf());
                    }
                }).match(LeaveGrid.class, msg -> {
                    if (userMap.containsKey(msg.userId)) {
                        userMap.remove(msg.userId);
                    }
                }).match(GetPlayers.class, msg -> {
                    Collection<IUser> values = userMap.values();
                    List<IUser> list = new ArrayList<IUser>();
                    list.addAll(values);
                    getSender().tell(list, getSelf());
                })
                .match(ScheduleTask.class, msg -> {
                    Scheduler scheduler = getContext().getSystem().scheduler();
                    if (msg.isOnce()) {
                        Cancellable cancellable = scheduler.scheduleOnce(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    } else {
                        Cancellable schedule = scheduler.schedule(Duration.create(msg.getDelay(), TimeUnit.MILLISECONDS), Duration.create(msg.getInterval(), TimeUnit.MILLISECONDS), msg.getTask(), getContext().getSystem().dispatcher());
                    }
                })
               .build();
    }




    public static class EnterGrid implements Serializable {

        private static final long serialVersionUID = -7809307785484209371L;
        public final IUser user;

        public EnterGrid(IUser user) {
            super();
            this.user = user;
        }

    }

    public static class LeaveGrid implements Serializable {
        private static final long serialVersionUID = 2793900887224969528L;
        public final String userId;

        public LeaveGrid(String userId) {
            super();
            this.userId = userId;
        }
    }

    public static class BroadCastAllUser implements Serializable {
        private static final long serialVersionUID = 2143027987941307508L;

        public final String jsonString;

        public BroadCastAllUser(String jsonString) {
            super();
            this.jsonString = jsonString;
        }

    }


    public static class GetPlayers implements Serializable {

        private static final long serialVersionUID = -6878647894314032793L;

    }
}
