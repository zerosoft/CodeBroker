package com.codebroker.protocol.serialization;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorRefResolver;
import akka.actor.typed.ActorSystem;
import com.codebroker.api.IGameUser;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.core.entities.GameUser;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.nio.charset.Charset;

public class IGameUserRefSerializer extends Serializer<IGameUser> {

	private static final ThreadLocal<IGameUserRefSerializer> kryoLocal = ThreadLocal.withInitial(() -> {
		IGameUserRefSerializer iObjectSerializer=new IGameUserRefSerializer();
		return iObjectSerializer;
	});

	public static IGameUserRefSerializer getInstance() {
		return kryoLocal.get();
	}


	@Override
	public void write(Kryo kryo, Output output, IGameUser object) {
		GameUser gameUser= (GameUser) object;
		String gameUserUid = gameUser.getUid();
		String actorRefString = gameUser.getActorRefString();
		output.writeInt(gameUserUid.getBytes().length);
		output.write(gameUserUid.getBytes(Charset.forName("UTF-8")));
		output.writeInt(actorRefString.getBytes().length);
		output.write(actorRefString.getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public IGameUser read(Kryo kryo, Input input, Class type) {
		int length = input.readInt();
		byte[] bytes = input.readBytes(length);
		String gameUserUid = new String(bytes,Charset.forName("UTF-8"));
		length = input.readInt();
		bytes = input.readBytes(length);
		String actorRefString = new String(bytes,Charset.forName("UTF-8"));
		ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
		ActorRef<IUserActor> objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(actorRefString);
		return new GameUser(gameUserUid,objectActorRef);
	}
}
