package com.codebroker.util;

import akka.actor.ActorRef;
import com.codebroker.core.data.CObject;
import com.codebroker.exception.CodeBrokerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static ActorRef elkLog = null;
    static boolean consolePrint = true;
    private static Logger avalonEngineLogger = LoggerFactory.getLogger("AvalonEngine");
    private static Logger exceptionLogger = LoggerFactory.getLogger("Exception");

    public static void enableConsole(boolean enable) {
        consolePrint = enable;
    }

    public static void debugLog(String debugInfo) {
        if (avalonEngineLogger.isDebugEnabled()) {
            avalonEngineLogger.debug(debugInfo);
        }
    }

    public static void snedELKLogMessage(String clazzName, String message) {
        try {
            if (elkLog != null) {
                CObject cObject = CObject.newInstance();
                cObject.putUtfString("c", clazzName);
                cObject.putUtfString("m", message);
                elkLog.tell(cObject, ActorRef.noSender());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void logPrintln(String msg) {
        avalonEngineLogger.info(msg);
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
