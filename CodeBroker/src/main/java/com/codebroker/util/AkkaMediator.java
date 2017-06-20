package com.codebroker.util;

import com.codebroker.core.ContextResolver;
import com.codebroker.core.manager.AkkaBootService;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Akka系统中介代理
 * 
 * @author xl
 *
 */
public class AkkaMediator {
	/**
	 * 获得通用信箱
	 * 
	 * @return
	 */
	public static Inbox getInbox() {
		AkkaBootService instance = ContextResolver.getComponent(AkkaBootService.class);
		return instance.getInbox();
	}

	public static ActorSystem getActorSystem() {
		AkkaBootService instance = ContextResolver.getComponent(AkkaBootService.class);
		return instance.getSystem();
	}

	/**
	 * 同步调用函数，阻塞的炒作。
	 * 
	 * @param actorRef
	 * @param object
	 * @return
	 * @throws Exception
	 *             超时异常
	 */
	public static <T> T getCallBak(ActorRef actorRef, Object object) throws Exception {
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
		Future<Object> future = Patterns.ask(actorRef, object, timeout);
		T result = (T) Await.result(future, timeout.duration());
		return result;
	}

	/**
	 * 查找根目录级别Actor
	 * 
	 * @param path
	 * @return
	 */
	public static ActorSelection getSystemActorSelection(String path) {
		ActorSelection actorSelection = getActorSystem().actorSelection("/user/" + path);
		return actorSelection;
	}

	public static String getFixSupervisorPath(String address, String identity) {
		return address + "/user/" + identity;
	}

	public static ActorSelection getRemoteActorSelection(String remotePath) {
		ActorSelection actorSelection = getActorSystem().actorSelection(remotePath);
		return actorSelection;
	}

}
