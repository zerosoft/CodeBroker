package com.codebroker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codebroker.core.data.CObject;

import akka.actor.ActorRef;

public class LogUtil {
	private static Logger avalonEngineLogger = LoggerFactory.getLogger("AvalonEngine");
	public static ActorRef elkLog = null;

	public static void debugLog(String debugInfo) {
		if (avalonEngineLogger.isDebugEnabled()) {
			avalonEngineLogger.debug(debugInfo);
		}
	}

	public static void snedELKLogMessage(String clazzName, String message) {
		try {
			if (elkLog != null) {
				CObject cObject=CObject.newInstance();
				cObject.putUtfString("c", clazzName);
				cObject.putUtfString("m", message);
				elkLog.tell(cObject, ActorRef.noSender());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
