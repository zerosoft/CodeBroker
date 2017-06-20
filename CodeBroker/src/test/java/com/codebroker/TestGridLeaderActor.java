package com.codebroker;

import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.codebroker.api.IArea;
import com.codebroker.core.actor.AreaManagerActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.testkit.javadsl.TestKit;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class TestGridLeaderActor {
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
		  try {
			new TestKit(system){{
				  final ActorRef actorOf = system.actorOf(Props.create(AreaManagerActor.class));
				  
				  Timeout timeout = new Timeout(Duration.create(59, "seconds"));
				  Future<Object> future = Patterns.ask(actorOf, new AreaManagerActor.CreateArea(1), timeout);
				  IArea result = (IArea) Await.result(future, timeout.duration());
				  System.out.println(result);
				  
				  {
					  timeout = new Timeout(Duration.create(59, "seconds"));
					  future = Patterns.ask(actorOf, new AreaManagerActor.CreateArea(1), timeout);
					  IArea result1 = (IArea) Await.result(future, timeout.duration());
					  
					  System.out.println(result.equals(result1));
				  }
				  
				  actorOf.tell(new AreaManagerActor.CreateArea(2), ActorRef.noSender());
				  actorOf.tell(new AreaManagerActor.CreateArea(3), ActorRef.noSender());

				  actorOf.tell(new AreaManagerActor.CreateArea(5), ActorRef.noSender());
				  
				  actorOf.tell(new AreaManagerActor.RemoveArea(2), ActorRef.noSender());
				  
				  {
					  timeout = new Timeout(Duration.create(59, "seconds"));
					  future = Patterns.ask(actorOf, new AreaManagerActor.GetAllArea(), timeout);
					  Collection<IArea> result1 = (Collection<IArea>) Await.result(future, timeout.duration());
					  
					  for (IArea grid : result1) {
						System.out.println(grid.getId());
					}
				  }
			  }};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
}
