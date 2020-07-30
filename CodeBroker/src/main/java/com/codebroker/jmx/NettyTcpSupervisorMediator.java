package com.codebroker.jmx;

import com.codebroker.jmx.annotations.ManagedAnnotation;
import com.codebroker.jmx.annotations.ManagedDescription;

public class NettyTcpSupervisorMediator extends MBean<NettyTcpSupervisorControl> {

    private NettyTcpSupervisorControl managedObject;

    protected NettyTcpSupervisorMediator(NettyTcpSupervisorControl managedObject, ManagementService service) {
        super(managedObject, service);
        objectName = service.createObjectName("IoMonitorMediator", "NettyTcpSupervisorControl");
        this.managedObject = managedObject;
    }

    @ManagedAnnotation("SessionNum")
    @ManagedDescription("Netty Session Num")
    public int getTransportNum() {
        return managedObject.onlineSessionNum();
    }

    @ManagedAnnotation("totalReciveflow")
    @ManagedDescription("Netty Session recive totalflow")
    public double totalReciveflow() {
        return managedObject.totalReciveflow();
    }

    @ManagedAnnotation("averageReciveflow")
    @ManagedDescription("Netty Session recive averageflow")
    public double averageReciveflow() {
        return managedObject.averageReciveflow();
    }

    @ManagedAnnotation("totalWriteflow")
    @ManagedDescription("Netty Session recive totalflow")
    public double totalWriteflow() {
        return managedObject.totalWriteflow();
    }

    @ManagedAnnotation("averageWriteflow")
    @ManagedDescription("Netty Session recive averageflow")
    public double averageWriteflow() {
        return managedObject.averageWriteflow();
    }
}
