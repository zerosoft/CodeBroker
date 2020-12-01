package com.codebroker.protocol.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.Charset;

public class GsonSerializer extends Serializer<JsonObject> {

	Gson gson=new Gson();

	private static final ThreadLocal<GsonSerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		GsonSerializer iObjectSerializer=new GsonSerializer();
		return iObjectSerializer;
	});

	public static GsonSerializer getInstance() {
		return kryoLocal.get();
	}


	@Override
	public void write(Kryo kryo, Output output, JsonObject object) {
		String string = object.toString();
		byte[] bytes = string.getBytes(Charset.forName(KryoSerialization.DEFAULT_ENCODING));
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	@Override
	public JsonObject read(Kryo kryo, Input input, Class type) {
		int length = input.readInt();
		byte[] bytes = input.readBytes(length);
		String json=new String(bytes,Charset.forName(KryoSerialization.DEFAULT_ENCODING));
		JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		return jsonObject;
	}
}
