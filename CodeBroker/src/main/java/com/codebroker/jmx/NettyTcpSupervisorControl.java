package com.codebroker.jmx;

public interface NettyTcpSupervisorControl {

    int onlineSessionNum();

    double totalReciveflow();

    double averageReciveflow();

    double totalWriteflow();

    double averageWriteflow();

}
