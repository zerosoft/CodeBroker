package com.codebroker.api.internal;

/**
 * 内部上下文.
 *
 * @author ZERO
 */
public class InternalContext {

    private static volatile ManagerLocator managerLocator;

    private InternalContext() {
    }

    public static ManagerLocator getManagerLocator() {
        ManagerLocator locator = managerLocator;
        if (locator == null) {
            throw new IllegalStateException("ManagerLocator is not set");
        }
        return locator;
    }

    public static synchronized void setManagerLocator(ManagerLocator managerLocator) {
        InternalContext.managerLocator = managerLocator;
    }
}
