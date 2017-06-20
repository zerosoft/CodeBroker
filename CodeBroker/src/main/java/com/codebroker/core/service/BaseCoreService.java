package com.codebroker.core.service;

import java.util.concurrent.atomic.AtomicInteger;

import com.codebroker.api.internal.IService;

public abstract class BaseCoreService implements IService {

	private static final AtomicInteger serviceId = new AtomicInteger(0);
	private static final String DEFAULT_NAME = "AnonymousService-";
	protected String name;
	protected volatile boolean active = false;

	@Override
	public void init(Object obj) {
		this.name = getId();
		this.active = true;
	}

	@Override
	public void destroy(Object obj) {
		this.active = false;
	}

	@Override
	public void handleMessage(Object obj) {
		throw new UnsupportedOperationException("This method should be overridden by the child class!");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return this.active;
	}

	protected static String getId() {
		return DEFAULT_NAME + serviceId.getAndIncrement();
	}

	public String toString() {
		return "[Core Service]: " + this.name + ", State: " + (isActive() ? "active" : "not active");
	}
}
