package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.codebroker.api.NPCControl;
import com.codebroker.core.entities.User;
import com.codebroker.core.local.WorldCreateNPC;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.usermanager.RemoveUser;

import java.util.Map;
import java.util.TreeMap;

public class NPCManagerActor extends AbstractActor {

    public static final String IDENTIFY = NPCManagerActor.class.getSimpleName().toString();
    private final ActorRef worldRef;
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private Map<String, ActorRef> npcRefMap = new TreeMap<String, ActorRef>();

    public NPCManagerActor(ActorRef worldRef) {
        super();
        this.worldRef = worldRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(byte[].class, msg -> {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        //移除玩家
                        case USER_MANAGER_REMOVE_USER:
                            RemoveUser removeUser = new RemoveUser();
                            thriftSerializerFactory.deserialize(removeUser, actorMessage.messageRaw);
                            removeUser(removeUser.userID);
                            break;
                        default:
                            break;
                    }
                })
                .match(WorldCreateNPC.class, msg -> {
                    createNPCUser(msg.NPCId, msg.control);
                })
                .build();
    }


    private void removeUser(String userId) {
        npcRefMap.remove(userId);
    }

    private void createNPCUser(String npcid, NPCControl control) {
        if (npcRefMap.containsKey(npcid)) {
            return;
        }
        User user = new User();
        ActorContext context = getContext();

        user.setUserId(npcid);

        ActorRef actorOf = context.actorOf(Props.create(UserActor.class, npcid, user, control, getSelf()), npcid);
        user.setActorRef(actorOf);
        //放入
        npcRefMap.put(npcid, actorOf);
    }


}
