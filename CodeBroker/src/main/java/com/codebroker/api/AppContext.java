package com.codebroker.api;

import com.codebroker.api.internal.IService;
import com.codebroker.api.internal.InternalContext;
import com.codebroker.core.ContextResolver;
import com.codebroker.exception.ManagerNotFoundException;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;

import java.util.Optional;


/**
 * 引擎应用的上下文.
 *
 * @author LongJu
 */
public final class AppContext {

    private AppContext() {
    }

    /**
     * 获取当前JVM的服务
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getManager(Class<T> type) {
        try {
            return InternalContext.getManagerLocator().getManager(type);
        } catch (IllegalStateException ise) {
            throw new ManagerNotFoundException("ManagerLocator is " + "unavailable", ise);
        }
    }

    /**
     * 设置当前JVM的服务
     *
     * @param service
     * @return
     */
    public static void setComponent(IService service) {
        InternalContext.getManagerLocator().setComponent(service);
    }

    public static <T> Optional<T> getComponent(Class<T> type) {
        try {
            return InternalContext.getManagerLocator().getComponent(type);
        } catch (IllegalStateException ise) {
            throw new ManagerNotFoundException("ManagerLocator is " + "unavailable", ise);
        }
    }

    /**
     * 设置当前JVM的服务
     *
     * @param service
     * @return
     */
    public static boolean setManager(IService service) {
        return InternalContext.getManagerLocator().setManager(service);
    }

    /**
     * 获得游戏世界的API
     *
     * @return
     */
    public static IGameWorld getGameWorld() {
        return InternalContext.getManagerLocator().getGameWorld();
    }

    /**
     * 获得静态文件配置的服务器ID
     *
     * @return
     */
    public static int getServerId() {
        PropertiesWrapper propertiesWrapper = ContextResolver.getPropertiesWrapper();
        return propertiesWrapper.getIntProperty(SystemEnvironment.APP_ID, 1);
    }
}
