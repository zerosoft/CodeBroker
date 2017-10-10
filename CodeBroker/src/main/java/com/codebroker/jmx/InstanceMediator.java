package com.codebroker.jmx;

import static com.codebroker.jmx.ManagementService.quote;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import com.codebroker.core.ServerEngine;
import com.codebroker.core.proxy.NettyTcpSupervisorProxy;
import com.codebroker.net.IoMonitorImpl;

import javassist.CannotCompileException;
import javassist.NotFoundException;

@ManagedDescription("CodeBrokerDesc")
public class InstanceMediator extends MBean<ServerEngine> {
	private static final int INITIAL_CAPACITY = 3;

	protected InstanceMediator(ServerEngine avalonInstance, ManagementService managementService) {
		super(avalonInstance, managementService);
		createProperties(avalonInstance);
		createMBeans(managementService);
		registerMBeans();
	}
	/**
	 * 注册MBean
	 * @param managementService
	 */
	private void createMBeans(ManagementService managementService) {
		IoMonitorImpl impl = new IoMonitorImpl();
		MBean control = new IoMonitorMediator(impl, managementService);
		register(control);

		NettyTcpSupervisorProxy proxy = NettyTcpSupervisorProxy.getInstance();
		MBean transport = new NettyTcpSupervisorMediator(proxy, managementService);
		register(transport);
	}

	private void registerMBeans() {

	}

	private void createProperties(ServerEngine engine) {
		Hashtable<String, String> properties = new Hashtable<String, String>(INITIAL_CAPACITY);
		properties.put("type", quote("CodeBroker"));
		properties.put("name", quote(engine.getClass().getSimpleName()));
		setObjectName(properties);
	}

	public InstanceMXBean getHazelcastInstance() {
		return managedObject;
	}

	@ManagedAnnotation("name")
	@ManagedDescription("Name of the Instance")
	public String getName() {
		return managedObject.getName();
	}

	@ManagedAnnotation(value = "stopEngine", operation = true)
	@ManagedDescription("stopEngine")
	public void stopEngine() {
		managedObject.stopEngine();
	}


	@ManagedAnnotation(value = "reloadClazz", operation = true)
	@ManagedDescription("reload one class")
	public void reloadClazz(String fileName, String clazzName) throws FileNotFoundException, IOException {
		managedObject.reloadClazz(fileName, clazzName);
	}

	@ManagedAnnotation(value = "reloadJar", operation = true)
	@ManagedDescription("reload jar in the reload doc")
	public void reloadJar(String fileName) throws FileNotFoundException, IOException {
		managedObject.reloadJar(fileName);
	}

	@ManagedAnnotation(value = "reloadMethod", operation = true)
	@ManagedDescription("reload one class method")
	public void reloadMethod(String clazz, String methodName, String context)throws NotFoundException, CannotCompileException, IOException {
		managedObject.reloadMethod(clazz, methodName, context);
	}
}
