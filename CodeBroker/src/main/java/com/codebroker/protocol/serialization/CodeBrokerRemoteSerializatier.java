package com.codebroker.protocol.serialization;

import akka.serialization.SerializerWithStringManifest;
import com.codebroker.core.actortype.message.IService;
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
    private final String HANDLE_MESSAGE = "HandleMessage";
    private final String KRYO = "KRYO";
    DefaultIDataSerializer s = DefaultIDataSerializer.getInstance();

    @Override
    public Object fromBinary(byte[] bs, String string) {
        if (string.equals(IObject)) {
            return s.binary2object(bs);
        } else if (string.equals(IArray)) {
            return s.binary2array(bs);
        } else if (string.equals(HANDLE_MESSAGE)) {
            return s.binary2HandleMessage(bs);
        }
        return null;
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
        } else if (object instanceof IService.HandleMessage){
            return HANDLE_MESSAGE;
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
        else if (object instanceof IService.HandleMessage) {
            return s.handleMessage2binary((IService.HandleMessage) object);
        }
        else {
            return s.Event2binary(object);
        }
//        else {
//            throw new IllegalArgumentException("Unknown type: " + object);
//        }
    }


}
