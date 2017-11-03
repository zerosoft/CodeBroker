package com.codebroker.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.codebroker.core.entities.User;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.setting.PrefixConstant;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.session.ReBindUser;
import com.message.thrift.actor.session.UserConnect2Server;
import com.message.thrift.actor.usermanager.CreateUserWithSession;
import com.message.thrift.actor.usermanager.RemoveUser;
import com.message.thrift.actor.usermanager.SetReBindKey;
import org.apache.thrift.TException;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserManagerActor extends AbstractActor {

    public static final String IDENTIFY = UserManagerActor.class.getSimpleName().toString();

    private static final AtomicInteger USER_ID = new AtomicInteger(1);

    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private Map<String, ActorRef> userRefMap = new TreeMap<String, ActorRef>();
    private Map<String, String> rebindKeyUserMap = new TreeMap<String, String>();



    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(byte[].class, (byte[] msg) -> {
                    ActorMessage actorMessage = thriftSerializerFactory.getActorMessage(msg);
                    switch (actorMessage.op) {
                        //创建网络玩家
                        case USER_MANAGER_CREATE_USER_WITH_SESSION:
                            CreateUserWithSession createUserWithSession = new CreateUserWithSession();
                            thriftSerializerFactory.deserialize(createUserWithSession, actorMessage.messageRaw);
                            createWorldUser(createUserWithSession.reBindKey,createUserWithSession.name,createUserWithSession.parms, getSender());
                            break;
                        //移除玩家
                        case USER_MANAGER_REMOVE_USER:
                            RemoveUser removeUser = new RemoveUser();
                            thriftSerializerFactory.deserialize(removeUser, actorMessage.messageRaw);
                            removeUser(removeUser.userID);
                            break;
                        //会话从新绑定玩家
                        case USER_MANAGER_SESSION_REBING_USER:
                            SetReBindKey bindKey = new SetReBindKey();
                            thriftSerializerFactory.deserialize(bindKey, actorMessage.messageRaw);
                            rebindKeyUserMap.put(bindKey.reBindKey, bindKey.userId);
                            break;
                        default:

                            break;
                    }
                })
                .match(FindUserByRebindKey.class, msg -> {
                    processReBind(msg);
                }).build();
    }

    private void createWorldUser(String reBindKey, String name, String parms, ActorRef iosessionRef) {
        int id = USER_ID.incrementAndGet();
        User user = new User();
        ActorRef actorOf = null;
        ActorContext context = getContext();

        String userid = PrefixConstant.USER_PRFIX + id;
        user.setUserId(userid);
        user.setLoginName(name);
        user.setLoginParms(parms);
        UserConnect2Server connect2Server = new UserConnect2Server();
        try {
            actorOf = context.actorOf(Props.create(UserActor.class, user, iosessionRef, getSelf()), userid);
            user.setActorRef(actorOf);
            userRefMap.put(userid, actorOf);
            connect2Server.success = true;
            connect2Server.bindingkey = reBindKey;
        } catch (Exception e) {
            connect2Server.success = false;
            connect2Server.bindingkey = "";
        }
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.SESSION_USER_CONNECT_TO_SERVER, connect2Server);
        iosessionRef.tell(actorMessageWithSubClass, actorOf);

    }

    /**
     * 处理重连接
     *
     * @param msg
     * @throws TException
     */
    private void processReBind(FindUserByRebindKey msg) throws TException {
        if (rebindKeyUserMap.containsKey(msg.bindKey)) {
            String key = rebindKeyUserMap.get(msg.bindKey);
            if (userRefMap.containsKey(key)) {
                ActorRef user = userRefMap.get(key);

                user.tell(thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_RE_BINDUSER_IOSESSION_ACTOR), getSender());

                ReBindUser reBindUser = new ReBindUser(true, key);
                byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.SESSION_REBIND_USER, reBindUser);
                getSender().tell(actorMessageWithSubClass, user);
            } else {
                ReBindUser reBindUser = new ReBindUser(false, "");
                byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.SESSION_REBIND_USER, reBindUser);
                getSender().tell(actorMessageWithSubClass, ActorRef.noSender());
            }
        } else {
            ReBindUser reBindUser = new ReBindUser(false, "");
            byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.SESSION_REBIND_USER, reBindUser);

            getSender().tell(actorMessageWithSubClass, ActorRef.noSender());
        }
    }


    private void removeUser(String userId) {
        userRefMap.remove(userId);
    }


    public static class FindUserByRebindKey {
        public final String bindKey;

        public FindUserByRebindKey(String bindKey) {
            super();
            this.bindKey = bindKey;
        }

    }
}
