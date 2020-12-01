package com.codebroker.protocol.serialization;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.ActorSystem;
import com.codebroker.api.IGameUser;
import com.codebroker.api.internal.IService;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.ServiceWithActor;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IServiceActor;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.core.entities.GameUser;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.nio.charset.Charset;

public class IServiceRefSerializer extends Serializer<IService> {

	private static final ThreadLocal<IServiceRefSerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		IServiceRefSerializer iObjectSerializer=new IServiceRefSerializer();
		return iObjectSerializer;
	});

	public static IServiceRefSerializer getInstance() {
		return kryoLocal.get();
	}


	@Override
	public void write(Kryo kryo, Output output, IService object) {
		ServiceWithActor serviceWithActor= (ServiceWithActor) object;
		String serviceWithActorName = serviceWithActor.getName();
		String actorRefString = serviceWithActor.getActorRefStringPath();

		output.writeInt(serviceWithActorName.getBytes().length);
		output.write(serviceWithActorName.getBytes(Charset.forName("UTF-8")));

		output.writeInt(actorRefString.getBytes().length);
		output.write(actorRefString.getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public IService read(Kryo kryo, Input input, Class type) {

		byte[] bytes = input.readBytes(input.readInt());
		String serviceWithActorName = new String(bytes,Charset.forName("UTF-8"));

		bytes = input.readBytes(input.readInt());
		String actorRefString = new String(bytes,Charset.forName("UTF-8"));

		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		ActorRef<IServiceActor> objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(actorRefString);

		return new ServiceWithActor(serviceWithActorName,objectActorRef);
	}
}
