package com.codebroker.core.entities;


import akka.actor.ActorRef;
import com.codebroker.api.IUser;
import com.codebroker.api.internal.ByteArrayPacket;
import com.codebroker.core.EventDispatcher;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.IObject;
import com.codebroker.protocol.BaseByteArrayPacket;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.codebroker.util.AkkaMediator;
import com.message.thrift.actor.ActorMessage;
import com.message.thrift.actor.Operation;
import org.apache.thrift.TException;

/**
 * 操作代理对象
 *
 * @author xl
 */
public class User extends EventDispatcher implements IUser {
    private final IObject iObject = CObjectLite.newInstance();
    ThriftSerializerFactory thriftSerializerFactory = new ThriftSerializerFactory();

    public String getUserId() {
        return iObject.getUtfString("userId");
    }

    public void setUserId(String userId) {
        iObject.putUtfString("userId", userId);
    }

    public boolean isNpc() {
        return getUserId().startsWith(UserManagerActor.NPC_PRFIX);
    }

    @Override
    public void sendMessageToIoSession(int requestId, Object message) {

        ActorMessage actorMessage = new ActorMessage();

        ByteArrayPacket byteArrayPacket = new BaseByteArrayPacket(requestId, (byte[]) message);
        actorMessage.messageRaw = byteArrayPacket.toByteBuffer();
        actorMessage.op = Operation.USER_SEND_PACKET_TO_IOSESSION;
        try {
            byte[] bytes = thriftSerializerFactory.getActorMessage(actorMessage);
            getActorRef().tell(bytes, ActorRef.noSender());
        } catch (TException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void disconnect() {
        try {
            getActorRef().tell(thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_DISCONNECT), ActorRef.noSender());
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            byte[] tbaseMessage = thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_IS_CONNECTED);
            return AkkaMediator.getCallBak(getActorRef(), tbaseMessage);
        } catch (Exception e) {
            return false;
        }
    }

    public void rebindIoSession(ActorRef actorRef) {
        try {
            getActorRef().tell(thriftSerializerFactory.getOnlySerializerByteArray(Operation.USER_RE_BINDUSER_IOSESSION_ACTOR), ActorRef.noSender());
        } catch (TException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IObject getIObject() {
        return iObject;
    }


}
