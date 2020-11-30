package com.codebroker.util;

import akka.actor.ActorRef;
import com.codebroker.exception.CodeBrokerException;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static ActorRef elkLog = null;
    static boolean consolePrint = true;
    private static Logger codeBrokerEngineLogger = LoggerFactory.getLogger("Engine");
    private static Logger exceptionLogger = LoggerFactory.getLogger("Exception");

    public static void enableConsole(boolean enable) {
        consolePrint = enable;
    }

    public static void debugLog(String debugInfo) {
        if (codeBrokerEngineLogger.isDebugEnabled()) {
            codeBrokerEngineLogger.debug(debugInfo);
        }
    }

    public static void snedELKLogMessage(String clazzName, String message) {
        try {
            if (elkLog != null) {
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("c", clazzName);
                jsonObject.addProperty("m", message);
                elkLog.tell(jsonObject, ActorRef.noSender());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void logPrintln(String msg) {
        codeBrokerEngineLogger.info(msg);
        if (consolePrint) {
            System.out.println(msg);
        }
    }

    public static synchronized void exceptionPrint(Exception e) {
        if (e != null) {
            e.printStackTrace();

            String stackMsg = CodeBrokerException.formatStackMsg(e);
            exceptionLogger.error(stackMsg);
        }
    }
}
