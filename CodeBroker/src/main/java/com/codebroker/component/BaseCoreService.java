package com.codebroker.component;

import com.codebroker.api.internal.ICoreService;
import com.codebroker.core.data.IObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基础服务 抽象类
 */
public abstract class BaseCoreService implements ICoreService {

    private static final AtomicInteger serviceId = new AtomicInteger(0);
    private static final String DEFAULT_NAME = "Anonymous-Service-";
    protected String name;
    protected volatile boolean active = false;

    protected static String getId() {
        return DEFAULT_NAME + serviceId.getAndIncrement();
    }

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
    public Object handleBackMessage(Object obj) {
        throw new UnsupportedOperationException("This method should be overridden by the child class!");
    }

    @Override
    public String getName() {
        return this.name;
    }


    public boolean isActive() {
        return this.active;
    }

    public void setActive() {
        this.active = true;
    }

    public String toString() {
        return "[Core Service]: " + this.name + ", State: " + (isActive() ? "active" : "not active");
    }
}
