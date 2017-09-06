package com.codebroker;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codebroker.core.actor.AreaActor;
import com.codebroker.core.actor.UserManagerActor;
import com.codebroker.core.entities.Area;
import com.codebroker.core.manager.UserManager;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;

public class TestGridActor {
	static ActorSystem system;
	 
	  @BeforeClass
	  public static void setup() {
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
				System.out.println(File.separatorChar);
				UserManager manager=new UserManager();
				final  ActorRef nmanager= system.actorOf(Props.create(UserManagerActor.class, manager),UserManagerActor.IDENTIFY);
				System.out.println(nmanager.path().toString());
				
				Area proxy=new Area();
				final ActorRef actorOf = system.actorOf(Props.create(AreaActor.class,nmanager));
				
				byte[] bb="hello".getBytes();
				
				actorOf.tell(bb, ActorRef.noSender());
			}};
	}
}
