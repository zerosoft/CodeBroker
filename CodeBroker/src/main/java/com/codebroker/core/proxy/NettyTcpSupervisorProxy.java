package com.codebroker.core.proxy;


import com.codebroker.jmx.NettyTcpSupervisorControl;

/**
 * 传输监听的代理累
 *
 * @author LongJu
 */
public class NettyTcpSupervisorProxy implements NettyTcpSupervisorControl {

    private int sessionNum;

    private double totalRedFLow;
    private double totalWriteFLow;


    public static NettyTcpSupervisorProxy getInstance() {

        return Inner.proxy;
    }

    public void addTransportNum() {
        this.sessionNum += 1;
    }

    public void subTransportNum() {
        this.sessionNum -= 1;

    }

    @Override
    public int onlineSessionNum() {
        return sessionNum;
    }

    @Override
    public double totalReciveflow() {
        return totalRedFLow;
    }

    public void addSessionReciveFlow(Long sessionId, Double flow, Long time) {
        totalRedFLow += flow;
    }

    public void addSessionWriteFlow(Long sessionId, Double flow, Long time) {
        totalWriteFLow += flow;
    }

    @Override
    public double averageReciveflow() {
        return totalRedFLow / sessionNum;

    }

    @Override
    public double totalWriteflow() {
        return totalWriteFLow;
    }

    @Override
    public double averageWriteflow() {
        return totalWriteFLow / sessionNum;
    }

    static class Inner {
        static NettyTcpSupervisorProxy proxy = new NettyTcpSupervisorProxy();
    }


}
