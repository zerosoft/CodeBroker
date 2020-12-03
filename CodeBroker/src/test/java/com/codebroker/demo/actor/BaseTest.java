package com.codebroker.demo.actor;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.typed.ActorRef;
import com.codebroker.api.IoSession;
import com.codebroker.core.actortype.Session;
import com.codebroker.core.actortype.User;
import com.codebroker.core.actortype.UserManager;
import com.codebroker.core.actortype.message.ISessionActor;
import com.codebroker.core.actortype.message.IUserManager;
import com.codebroker.core.data.CObject;
import com.codebroker.protocol.BaseByteArrayPacket;
import org.junit.AfterClass;
import org.junit.Test;

import java.time.Duration;

import static org.mockito.Mockito.*;

public class BaseTest {
	static final ActorTestKit testKit = ActorTestKit.create();


	@Test
	public void testSessionCreate() {

		IoSession session = mock(IoSession.class);
		BaseByteArrayPacket baseByteArrayPacket = mock(BaseByteArrayPacket.class);

		ActorRef<IUserManager> userManagerActorRef = testKit.spawn(UserManager.create(1, Duration.ofSeconds(10)));

		ActorRef<ISessionActor> iSessionActorRef = testKit.spawn(Session.create(1L, session,1));

		iSessionActorRef.tell(new ISessionActor.SessionActorAcceptRequest(baseByteArrayPacket));


		testKit.spawn(User.create("1",iSessionActorRef,userManagerActorRef));


		userManagerActorRef.tell(new IUserManager.SendEventToGameUser("test", CObject.newInstance(),null));
	}


	@AfterClass
	public static void cleanup() {
		testKit.shutdownTestKit();
	}
}
