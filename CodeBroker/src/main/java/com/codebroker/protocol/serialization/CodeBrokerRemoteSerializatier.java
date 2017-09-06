package com.codebroker.protocol.serialization;

import com.codebroker.core.data.IArray;
import com.codebroker.core.data.IObject;

import akka.serialization.SerializerWithStringManifest;
/**
 * 对象序列化
 * @author zero
 *
 */
public class CodeBrokerRemoteSerializatier extends SerializerWithStringManifest {
	DefaultSFSDataSerializer s = DefaultSFSDataSerializer.getInstance();

	private final String IObject = "IO";
	private final String IArray = "IA";
	
	
	@Override
	public Object fromBinary(byte[] bs, String string) {
		if (string.equals(IObject)) {
			return s.binary2object(bs);
		}else if (string.equals(IArray)) {
			return s.binary2array(bs);
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
		}
		return "";
	}

	@Override
	public byte[] toBinary(Object object) {
		if (object instanceof IObject) {
			return s.object2binary((IObject) object);
		} else if (object instanceof IArray) {
			return s.array2binary((IArray) object);
		} 
		else {
			throw new IllegalArgumentException("Unknown type: " + object);
		}
	}
	

}
