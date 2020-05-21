package com.codebroker.api;

import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.InternalContext;
import com.codebroker.core.ContextResolver;
import com.codebroker.exception.ManagerNotFoundException;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;


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

    public static IGameWorld getGameWorld(){
        return InternalContext.getManagerLocator().getGameWorld();
    }

    public static int getServerId(){
        PropertiesWrapper propertiesWrapper = ContextResolver.getPropertiesWrapper();
       return propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID, 1);
    }
}
