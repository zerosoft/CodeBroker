package com.codebroker.core;

import com.codebroker.component.ComponentRegistryImpl;
import com.codebroker.util.PropertiesWrapper;

final class StartupKernelContext extends KernelContext {

    /**
     * Instantiates a new startup kernel context.
     *
     * @param applicationName   the application name
     * @param systemRegistry    the system registry
     * @param propertieswrapper the propertieswrapper
     */
    public StartupKernelContext(String applicationName, ComponentRegistryImpl systemRegistry,
                                PropertiesWrapper propertieswrapper) {
        super(applicationName, systemRegistry, new ComponentRegistryImpl(), propertieswrapper);
    }

}
