package com.codebroker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codebroker.core.actor.CodeBrokerSystem;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.core.manager.UserManager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;

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
			  
			  try {} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		  };
	  }
}
