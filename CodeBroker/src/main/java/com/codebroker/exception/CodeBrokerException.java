package com.codebroker.exception;

import com.codebroker.util.LogUtil;

public class CodeBrokerException extends RuntimeException {


    private static final long serialVersionUID = -521355582712781650L;

    public CodeBrokerException() {
        super(CodeBrokerException.class.getName());
    }

    public CodeBrokerException(Throwable e) {
        super(CodeBrokerException.class.getName(), e);
    }

    public CodeBrokerException(String msg, Throwable e) {
        super(msg, e);
    }

    public CodeBrokerException(String msg) {
        super(CodeBrokerException.class.getName() + ":" + msg);
    }

    public CodeBrokerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public static synchronized void catchException(Exception e) {
        if (e != null) {
            LogUtil.exceptionPrint(e);

        }
    }

    public static String formatStackMsg(Exception e) {
        if (e != null) {
            StackTraceElement[] stackArray = e.getStackTrace();
            StringBuffer sb = new StringBuffer();
            sb.append(e.toString() + "\n");

            for (int i = 0; (stackArray != null) && (i < stackArray.length); i++) {
                StackTraceElement element = stackArray[i];
                sb.append(element.toString() + "\n");
            }

            return sb.toString();
        }
        return "";
    }

    public static String formatStackTrace(StackTraceElement[] stackArray, int skipCount) {
        if (stackArray != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = skipCount; i < stackArray.length; i++) {
                StackTraceElement element = stackArray[i];
                sb.append(element.toString() + "\n");
            }
            return sb.toString();
        }
        return "";
    }


}
