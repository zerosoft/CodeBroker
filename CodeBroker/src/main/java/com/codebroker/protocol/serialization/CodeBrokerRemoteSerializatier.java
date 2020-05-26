package com.codebroker.protocol.serialization;

import akka.actor.ExtendedActorSystem;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.javadsl.Adapter;
import akka.serialization.SerializerWithStringManifest;
import com.codebroker.core.data.IArray;
import com.codebroker.core.data.IObject;

/**
 * 对象序列化
 *
 * @author LongJu
 */
public class CodeBrokerRemoteSerializatier extends SerializerWithStringManifest {

    private final String IObject = "IO";
    private final String IArray = "IA";

    private final String KRYO = "KRYO";
    DefaultIDataSerializer s = DefaultIDataSerializer.getInstance();

    final ExtendedActorSystem system;
    final ActorRefResolver actorRefResolver;

    public CodeBrokerRemoteSerializatier(ExtendedActorSystem system){
        this.system = system;
        actorRefResolver = ActorRefResolver.get(Adapter.toTyped(system));
    }

    @Override
    public Object fromBinary(byte[] bs, String string) {
        if (string.equals(IObject)) {
            return s.binary2object(bs);
        } else if (string.equals(IArray)) {
            return s.binary2array(bs);
        } else {
            return s.binary2Event(bs);
        }
    }

    @Override
    public int identifier() {
        return 20170906;
    }

    @Override
    public String manifest(Object object) {
        if (object instanceof IObject) {
            return IObject;
        } else if (object instanceof IArray) {
            return IArray;
        }
        else  {
            return KRYO;
        }
    }

    @Override
    public byte[] toBinary(Object object) {
        if (object instanceof IObject) {
            return s.object2binary((IObject) object);
        } else if (object instanceof IArray) {
            return s.array2binary((IArray) object);
        }
        else {
            return s.Event2binary(object);
        }
    }


}
