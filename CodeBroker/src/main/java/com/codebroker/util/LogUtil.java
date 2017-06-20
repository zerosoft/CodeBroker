package com.codebroker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.core.ContextResolver;
import com.codebroker.core.actor.ELKLogActor;
import com.codebroker.core.actor.ELKLogActor.ELKSystemLog;
import com.codebroker.core.manager.AkkaBootService;

import akka.actor.ActorRef;

public class LogUtil {
	private static Logger avalonEngineLogger = LoggerFactory.getLogger("AvalonEngine");

	public static void debugLog(String debugInfo) {
		if (avalonEngineLogger.isDebugEnabled()) {
			avalonEngineLogger.debug(debugInfo);
		}
	}

	public static void snedELKLogMessage(String clazzName, String message) {
		AkkaBootService component = ContextResolver.getComponent(AkkaBootService.class);
		ActorRef localPath = component.getLocalPath(ELKLogActor.IDENTIFY);
		ELKLogActor.ELKSystemLog log = new ELKSystemLog();
		log.clazzName = clazzName;
		log.message = message;
		localPath.tell(log, ActorRef.noSender());
	}
}
