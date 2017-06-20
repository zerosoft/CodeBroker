package com.codebroker;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codebroker.api.IUser;
import com.codebroker.core.actor.CodeBrokerSystem;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.core.actor.UserManagerActor.GetUserList;
import com.codebroker.core.manager.UserManager;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class TestNUserManagerActor {
	static ActorSystem system;
	 
	  @BeforeClass
	  public static void setup() {
//		 File file=new File("D:\\Users\\xl\\workspace\\FlayShooting\\conf\\application.conf");
//		  Config cg = ConfigFactory.parseFile(file);
//			cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
//			Config config = ConfigFactory.load(cg).getConfig("CodeBroker");
//	    system = ActorSystem.create("CodeBroker",config);
	    system=ActorSystem.create();
	  }
	  
	  @AfterClass
	  public static void teardown() {
	    TestKit.shutdownActorSystem(system);
	    system = null;
	  }

	  @Test
	  public void testIt(){
		  new TestKit(system){{
			  UserManager manager=new UserManager();
			  final  ActorRef actorRef= system.actorOf(Props.create(UserManagerActor.class, manager),CodeBrokerSystem.IDENTIFY);
			  
			  try {
				Timeout timeout = new Timeout(Duration.create(59, "seconds"));
				Future<Object> future = Patterns.ask(actorRef, new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()), timeout);
				IUser result = (IUser) Await.result(future, timeout.duration());
				
				actorRef.tell(new UserManagerActor.RemoveUser(result.getUserId()), ActorRef.noSender());
				
				actorRef.tell(new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()),  ActorRef.noSender());
				actorRef.tell(new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()),  ActorRef.noSender());
				actorRef.tell(new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()),  ActorRef.noSender());
				
				actorRef.tell(new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()),  ActorRef.noSender());
				actorRef.tell(new UserManagerActor.CreateUser(false,UUID.randomUUID().toString()),  ActorRef.noSender());
				{
					timeout = new Timeout(Duration.create(59, "seconds"));
					future = Patterns.ask(actorRef, new UserManagerActor.GetUserList(GetUserList.Type.NPC), timeout);
					List<IUser> result1 = (List<IUser>) Await.result(future, timeout.duration());
					for (IUser iUser : result1) {
						System.out.println(iUser.getUserId()+"==NPC===");
					}
				}
				{
					timeout = new Timeout(Duration.create(59, "seconds"));
					future = Patterns.ask(actorRef, new UserManagerActor.GetUserList(GetUserList.Type.PLAYER), timeout);
					List<IUser> result1 = (List<IUser>) Await.result(future, timeout.duration());
					for (IUser iUser : result1) {
						System.out.println(iUser.getUserId()+"==PLAYER===");
					}
				}
				{
					timeout = new Timeout(Duration.create(59, "seconds"));
					future = Patterns.ask(actorRef, new UserManagerActor.GetUserList(GetUserList.Type.ALL), timeout);
					List<IUser> result1 = (List<IUser>) Await.result(future, timeout.duration());
					for (IUser iUser : result1) {
						System.out.println(iUser.getUserId()+"==ALL===");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		  };
	  }
}
