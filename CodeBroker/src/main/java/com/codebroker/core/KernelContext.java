package com.codebroker.core;

import akka.actor.typed.ActorSystem;
import com.codebroker.api.AppListener;
import com.codebroker.api.internal.ComponentRegistry;
import com.codebroker.api.internal.IService;
import com.codebroker.component.ComponentRegistryImpl;
import com.codebroker.core.actortype.message.IGameRootSystemMessage;
import com.codebroker.component.service.AkkaSystemComponent;
import com.codebroker.exception.ManagerNotFoundException;
import com.codebroker.util.PropertiesWrapper;

import java.util.MissingResourceException;
import java.util.Optional;

/**
 * 内核上下文
 *
 * @author LongJu
 */
class KernelContext {

    protected final PropertiesWrapper propertieswrapper;
    /**
     * 逻辑组件（业务层组件）
     */
    protected final ComponentRegistry managerComponents;
    /**
     * 系统服务组件
     */
    protected final ComponentRegistry serviceComponents;

    private final String applicationName;

    private AppListener appListener;


    KernelContext(KernelContext context) {
        this(context.applicationName, context.serviceComponents, context.managerComponents, context.propertieswrapper);
    }

    /**
     * Instantiates a new kernel context.
     *
     * @param applicationName   the application name 应用名称
     * @param serviceComponents the service components 系统提供的服务
     * @param managerComponents the manager components 逻辑服务提供
     * @param propertieswrapper the properties wrapper
     */
    protected KernelContext(String applicationName, ComponentRegistry serviceComponents,
                            ComponentRegistry managerComponents, PropertiesWrapper propertieswrapper) {
        this.applicationName = applicationName;
        this.serviceComponents = serviceComponents;
        this.managerComponents = managerComponents;
        this.propertieswrapper = propertieswrapper;
    }

    <T> Optional<T> getManager(Class<T> type) {
        try {
            return managerComponents.getComponent(type);
        } catch (MissingResourceException mre) {
            throw new ManagerNotFoundException("couldn't find manager: " + type.getName());
        }
    }

    <T> Optional<T> getComponent(Class<T> type) {
        return serviceComponents.getComponent(type);
    }


    public AppListener getAppListener() {
        return appListener;
    }

    public void setAppListener(AppListener appListener) {
        this.appListener = appListener;
    }

    public String toString() {
        return applicationName;
    }

    public PropertiesWrapper getPropertiesWrapper() {
        return propertieswrapper;
    }

    public void setManager(IService type) {
        ((ComponentRegistryImpl) managerComponents).addComponent(type);
    }

    public ActorSystem<IGameRootSystemMessage> getActorSystem() {
        Optional<AkkaSystemComponent> component = getComponent(AkkaSystemComponent.class);
        if (component.isPresent()){
            return component.get().getSystem();
        }
        throw new RuntimeException();
    }

}
