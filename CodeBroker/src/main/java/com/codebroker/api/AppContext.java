package com.codebroker.api;

import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.InternalContext;
import com.codebroker.exception.ManagerNotFoundException;


/**
 * 引擎应用的上下文.
 *
 * @author LongJu
 */
public final class AppContext {

    private AppContext() {
    }

    public static <T> T getManager(Class<T> type) {
        try {
            return InternalContext.getManagerLocator().getManager(type);
        } catch (IllegalStateException ise) {
            throw new ManagerNotFoundException("ManagerLocator is " + "unavailable", ise);
        }
    }

    public static void setManager(IService service) {
        InternalContext.getManagerLocator().setManager(service);
    }




}
