package com.codebroker.jmx;

/**
 * 网络监控.
 *
 * @author LongJu
 */
public interface IoMonitorControl {

    int getUnbindingSessionNum();

    int getBindingSessionNum();

    boolean disConnect(long sessionId);

    String getBindingSessionInfo(int index, int limit);

    String getUnBindingSessionInfo(int index, int limit);

}
