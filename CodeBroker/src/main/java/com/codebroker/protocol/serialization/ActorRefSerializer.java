package com.codebroker.protocol.serialization;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.ActorSystem;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.data.CObject;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ActorRefSerializer extends Serializer<ActorRef> {

	private static final ThreadLocal<ActorRefSerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		ActorRefSerializer iObjectSerializer=new ActorRefSerializer();
		return iObjectSerializer;
	});

	public static ActorRefSerializer getInstance() {
		return kryoLocal.get();
	}


	@Override
	public void write(Kryo kryo, Output output, ActorRef object) {
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		String objectActorRef = ActorRefResolver.get(actorSystem).toSerializationFormat(object);
		byte[] bytes = objectActorRef.getBytes();
		output.writeInt(bytes.length);
		output.write(bytes);
	}

	@Override
	public ActorRef read(Kryo kryo, Input input, Class type) {
		int length = input.readInt();
		byte[] bytes = input.readBytes(length);
		String serializedActorRef = new String(bytes);
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		ActorRef<Object> objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(serializedActorRef);
		return objectActorRef;
	}
}
