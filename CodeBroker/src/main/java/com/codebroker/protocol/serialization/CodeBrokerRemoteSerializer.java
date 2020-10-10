package com.codebroker.protocol.serialization;

import akka.actor.ExtendedActorSystem;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.javadsl.Adapter;
import akka.serialization.SerializerWithStringManifest;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IArray;
import com.codebroker.core.data.IObject;

/**
 * 对象序列化
 *
 * @author LongJu
 */
public class CodeBrokerRemoteSerializer extends SerializerWithStringManifest {

    private final String IObject = "IO";
    private final String IArray = "IA";
    private final String Kryo = "KRYO";

    DefaultIDataSerializer defaultIDataSerializer = DefaultIDataSerializer.getInstance();

    final ExtendedActorSystem system;
    final ActorRefResolver actorRefResolver;

    public CodeBrokerRemoteSerializer(ExtendedActorSystem system) {
        this.system = system;
        actorRefResolver = ActorRefResolver.get(Adapter.toTyped(system));
    }

    @Override
    public Object fromBinary(byte[] bs, String string) {
        if (string.equals(IObject)) {
            return defaultIDataSerializer.binary2object(bs);
        } else if (string.equals(IArray)) {
            return defaultIDataSerializer.binary2array(bs);
        } else {
            com.codebroker.core.data.IObject iObject = defaultIDataSerializer.binary2object(bs);
            Object pojo = defaultIDataSerializer.cbo2pojo(iObject);
            return pojo;
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
        } else {
            return Kryo;
        }
    }

    @Override
    public byte[] toBinary(Object object) {
        if (object instanceof IObject) {
            return defaultIDataSerializer.object2binary((IObject) object);
        } else if (object instanceof IArray) {
            return defaultIDataSerializer.array2binary((IArray) object);
        } else {
//            return defaultIDataSerializer.obj2binary(object);
            return defaultIDataSerializer.object2binary(CObject.newFromObject(object));
        }
    }
}