package com.codebroker.jmx;

@ManagedDescription("IoMonitorControl")
public class IoMonitorMediator extends MBean<IoMonitorControl> {

	private IoMonitorControl managedObject;

	protected IoMonitorMediator(IoMonitorControl managedObject, ManagementService service) {
		super(managedObject, service);
		objectName = service.createObjectName("IoMonitorMediator", "IoMonitorControl");
		this.managedObject = managedObject;
	}

	@ManagedAnnotation("name")
	@ManagedDescription("Name of the Object")
	public String getName() {
		return "IoMonitorMXBean";
	}

	@ManagedAnnotation("bindingNum")
	@ManagedDescription("session binding Num")
	public int getBindingSessionNum() {
		return managedObject.getBindingSessionNum();
	}

	@ManagedAnnotation("unBindingNum")
	@ManagedDescription("session unbinding Num")
	public int getUnbindingSessionNum() {
		return managedObject.getUnbindingSessionNum();
	}

	@ManagedAnnotation(value = "getBindingSessionInfo", operation = true)
	@ManagedDescription("getBindingSessionInfo")
	public String getBindingSessionInfo(int index, int limit) {
		return managedObject.getBindingSessionInfo(index, limit);
	}

	@ManagedAnnotation(value = "getUnBindingSessionInfo", operation = true)
	@ManagedDescription("getUnBindingSessionInfo")
	public String getUnBindingSessionInfo(int index, int limit) {
		return managedObject.getUnBindingSessionInfo(index, limit);
	}

}
