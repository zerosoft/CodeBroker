package com.codebroker;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codebroker.core.actor.CodeBrokerSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;


public class CodeBrokerTest{

	static ActorSystem system;
	 
	  @BeforeClass
	  public static void setup() {
		 File file=new File("D:\\Users\\xl\\workspace\\FlayShooting\\conf\\application.conf");
		  Config cg = ConfigFactory.parseFile(file);
			cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
			Config config = ConfigFactory.load(cg).getConfig("CodeBroker");
	    system = ActorSystem.create("CodeBroker",config);
	  }
	  
	  @AfterClass
	  public static void teardown() {
	    TestKit.shutdownActorSystem(system);
	    system = null;
	  }

	  @Test
	  public void testIt(){
		  new TestKit(system){{
			  final  ActorRef actorRef= system.actorOf(Props.create(CodeBrokerSystem.class, system),CodeBrokerSystem.IDENTIFY);
			  actorRef.tell(new CodeBrokerSystem.InitAkkaSystem(), ActorRef.noSender());
		  }
		  };
	  }
}
