package com.codebroker.demo.actor;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import com.codebroker.api.IoSession;
import com.codebroker.core.actortype.Session;
import com.codebroker.core.actortype.User;
import com.codebroker.core.actortype.UserManager;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.core.data.CObject;
import com.codebroker.core.entities.GameUser;
import com.codebroker.protocol.BaseByteArrayPacket;
import org.junit.AfterClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class BaseTest {
	static final ActorTestKit testKit = ActorTestKit.create();


	@Test
	public void testSessionCreate() {

		IoSession session = mock(IoSession.class);
		BaseByteArrayPacket baseByteArrayPacket = mock(BaseByteArrayPacket.class);

		ActorRef<IUserManager> userManagerActorRef = testKit.spawn(UserManager.create(1));

		ActorRef<ISession> iSessionActorRef = testKit.spawn(Session.create(1L, session));

		iSessionActorRef.tell(new ISession.SessionAcceptRequest(baseByteArrayPacket));


		testKit.spawn(User.create("1",iSessionActorRef,userManagerActorRef));


		userManagerActorRef.tell(new IUserManager.SendMessageToGameUser("test", CObject.newInstance(),null));
	}


	@AfterClass
	public static void cleanup() {
		testKit.shutdownTestKit();
	}
}
