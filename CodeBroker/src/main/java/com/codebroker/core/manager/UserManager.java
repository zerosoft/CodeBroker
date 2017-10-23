package com.codebroker.core.manager;

import akka.actor.ActorRef;
import com.codebroker.api.IUser;
import com.codebroker.api.manager.IUserManager;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.Operation;
import com.message.thrift.actor.usermanager.CreateUser;
import com.message.thrift.actor.usermanager.RemoveUser;

import java.util.List;
import java.util.UUID;

/**
 * 用户管理器API
 *
 * @author ZERO
 */
public class UserManager implements IUserManager {
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();
    private ActorRef managerRef;

    @Override
    public void createUser(boolean npc) throws Exception {
        CreateUser createUser = new CreateUser(npc, UUID.randomUUID().toString(), null);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_MANAGER_CREATE_USER, createUser);

        managerRef.tell(actorMessageWithSubClass, ActorRef.noSender());
    }

    @Override
    public void removeUser(String userId) {
        RemoveUser removeUser = new RemoveUser(userId);
        byte[] actorMessageWithSubClass = thriftSerializerFactory.getActorMessageByteArray(Operation.USER_MANAGER_REMOVE_USER, removeUser);

        managerRef.tell(actorMessageWithSubClass, ActorRef.noSender());
    }

    public ActorRef getManagerRef() {
        return managerRef;
    }

    public void setManagerRef(ActorRef managerRef) {
        this.managerRef = managerRef;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<IUser> getAllUser() throws Exception {
        return null;
    }

    @Override
    public IUser getPlayerUser(String userId) throws Exception {
        return null;
    }

}
