package com.codebroker.jmx;

public interface NettyTcpSupervisorControl {

    public int onlineSessionNum();

    public double totalReciveflow();

    public double averageReciveflow();

    public double totalWriteflow();

    public double averageWriteflow();

}
