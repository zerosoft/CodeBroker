package com.codebroker.jmx;

public class TransportSupervisorMediator extends MBean<TransportSupervisorControl> {

	private TransportSupervisorControl managedObject;

	protected TransportSupervisorMediator(TransportSupervisorControl managedObject, ManagementService service) {
		super(managedObject, service);
		objectName = service.createObjectName("TransportSupervisorMediator", "TransportSupervisorControl");
		this.managedObject = managedObject;
	}

	@ManagedAnnotation("transportNum")
	@ManagedDescription("Transport actor Num")
	public int getTransportNum() {
		return managedObject.getTransportNum();
	}

}
