package com.codebroker.protocol.serialization;

import com.codebroker.core.data.CObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IObjectSerializer extends Serializer {

	private static final ThreadLocal<IObjectSerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		IObjectSerializer iObjectSerializer=new IObjectSerializer();
		return iObjectSerializer;
	});

	public static IObjectSerializer getInstance() {
		return kryoLocal.get();
	}


	@Override
	public void write(Kryo kryo, Output output, Object object) {
		CObject cObject= (CObject) object;
		byte[] bytes = cObject.toBinary();
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	@Override
	public Object read(Kryo kryo, Input input, Class type) {
		int length = input.readInt();
		byte[] bytes = input.readBytes(length);
		CObject cObject = CObject.newFromBinaryData(bytes);
		return cObject;
	}
}
