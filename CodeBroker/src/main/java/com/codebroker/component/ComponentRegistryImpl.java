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
 * @author ZERO
 */
public class ComponentRegistryImpl implements ComponentRegistry {

    /**
     * The logger.
     */
    private Logger logger = LoggerFactory.getLogger("AvalonEngine");

    /**
     * 存放组件.
     */
    private LinkedHashSet<IService> componentSet;

    /**
     * Instantiates a new component registry impl.
     */
    public ComponentRegistryImpl() {
        logger.debug("ComponentRegistryImpl create");
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

        for (Object component : componentSet) {
            if (type.isAssignableFrom(component.getClass())) {
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

    /**
     * Adds the component.
     *
     * @param component the component
     */
    public void addComponent(IService component) {
        componentSet.add(component);
        logger.debug("Component add component " + component.toString());
    }

}
