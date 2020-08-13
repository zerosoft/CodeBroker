package com.codebroker.component;

import com.codebroker.api.internal.ComponentRegistry;
import com.codebroker.api.internal.IService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;

/**
 * 组件管理器的基本实现.
 *
 * @author LongJu
 */
public class ComponentRegistryImpl implements ComponentRegistry {

    private Logger logger = LoggerFactory.getLogger(ComponentRegistryImpl.class.getSimpleName());

    /**
     * 存放组件.
     */
    private LinkedHashSet<IService> componentSet;

    public ComponentRegistryImpl() {
        logger.debug("init = ComponentRegistryImpl");
        componentSet = Sets.newLinkedHashSet();
    }

    @Override
    public Iterator<IService> iterator() {
        return Collections.unmodifiableSet(componentSet).iterator();
    }

    @Override
    public <T> T getComponent(Class<T> type) {
        // 目标组件
        Object matchComponent = null;

        for (IService component : componentSet) {
            if (type.isAssignableFrom(component.getClass())) {
                if (matchComponent != null) {
                    throw new MissingResourceException("More than one matching component", type.getName(), null);
                }
                matchComponent = component;
            }else if (type.getName()==component.getName()){
                if (matchComponent != null) {
                    throw new MissingResourceException("More than one matching component", type.getName(), null);
                }
                matchComponent = component;
            }
        }

        if (matchComponent == null) {
            throw new MissingResourceException("No matching components", type.getName(), null);
        }

        return type.cast(matchComponent);
    }

    @Override
    public void removeComponent(Class type) {
        for (IService component : componentSet) {
            if (type.isAssignableFrom(component.getClass())) {
                componentSet.remove(component);
                return;
            }else if (type.getName()==component.getName()){
                componentSet.remove(component);
                return;
            }
        }
    }

    public void addComponent(IService component) {
        componentSet.add(component);
        logger.debug("Component add component " + component.toString());
    }

}
