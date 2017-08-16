package com.codebroker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.core.actor.ELKLogActor;
import com.codebroker.core.actor.ELKLogActor.ELKSystemLog;

import akka.actor.ActorRef;

public class LogUtil {
	private static Logger avalonEngineLogger = LoggerFactory.getLogger("AvalonEngine");
	public static ActorRef elkLog=null;
	
	
	public static void debugLog(String debugInfo) {
		if (avalonEngineLogger.isDebugEnabled()) {
			avalonEngineLogger.debug(debugInfo);
		}
	}

	public static void snedELKLogMessage(String clazzName, String message) {
		try {
			if (elkLog!=null) {
				ELKLogActor.ELKSystemLog log = new ELKSystemLog();
				log.clazzName = clazzName;
				log.message = message;
				elkLog.tell(log, ActorRef.noSender());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
