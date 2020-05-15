package com.codebroker.protocol.serialization;

import akka.serialization.SerializerWithStringManifest;
import com.codebroker.api.event.Event;
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
    private final String EVENT_MESSAGE = "EM";
    DefaultSFSDataSerializer s = DefaultSFSDataSerializer.getInstance();

    @Override
    public Object fromBinary(byte[] bs, String string) {
        if (string.equals(IObject)) {
            return s.binary2object(bs);
        } else if (string.equals(IArray)) {
            return s.binary2array(bs);
        } else if (string.equals(EVENT_MESSAGE)) {
            return s.binary2Event(bs);
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
        } else if (object instanceof Event) {
            return EVENT_MESSAGE;
        }
        return "";
    }

    @Override
    public byte[] toBinary(Object object) {
        if (object instanceof IObject) {
            return s.object2binary((IObject) object);
        } else if (object instanceof IArray) {
            return s.array2binary((IArray) object);
        } else if (object instanceof Event) {
            return s.Event2binary((Event) object);
        } else {
            throw new IllegalArgumentException("Unknown type: " + object);
        }
    }


}
