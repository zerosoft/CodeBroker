package com.codebroker.jmx;

import com.codebroker.core.ServerEngine;
import com.codebroker.setting.SystemEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Pattern;

public class ManagementService {

    //顶级目录
    static final String DOMAIN = SystemEnvironment.ENGINE_NAME;
    private static final int INITIAL_CAPACITY = 3;
    private static Logger logger = LoggerFactory.getLogger(ManagementService.class);
    final InstanceMXBean instance;
    private final InstanceMediator instanceMBean;

    public ManagementService(ServerEngine instance) {
        logger.debug("ManagementService init");
        this.instance = instance;
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        InstanceMediator instanceMBean;
        try {
            instanceMBean = new InstanceMediator(instance, this);
            mbs.registerMBean(instanceMBean, instanceMBean.objectName);
        } catch (Exception e) {
            instanceMBean = null;
            logger.warn("ManagementService error", e);
        }
        this.instanceMBean = instanceMBean;
    }

    public static void shutdownAll() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName(DOMAIN + ":*");
            Set<ObjectName> entries = mbs.queryNames(objectName, null);
            for (ObjectName name : entries) {
                if (mbs.isRegistered(name)) {
                    mbs.unregisterMBean(name);
                }
            }
        } catch (Exception e) {
            logger.warn("InstanceMediator shutdownAll error", e);
        }
    }

    public static String quote(String text) {
        return Pattern.compile("[:\",=*?]").matcher(text).find() ? ObjectName.quote(text) : text;
    }

    public InstanceMediator getInstanceMBean() {
        return instanceMBean;
    }

    public void destroy() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            String quote = quote(instance.getName());
            ObjectName objectName = new ObjectName(DOMAIN + ":instance=" + quote + ",*");
            Set<ObjectName> entries = mbs.queryNames(objectName, null);
            for (ObjectName name : entries) {
                if (mbs.isRegistered(name)) {
                    mbs.unregisterMBean(name);
                }
            }
        } catch (Exception e) {
            logger.warn("ManagementService destroy error", e);
        }
    }

    protected ObjectName createObjectName(String type, String name) {
        Hashtable<String, String> properties = new Hashtable<>(INITIAL_CAPACITY);
        if (type != null) {
            properties.put("type", quote(type));
        }
        if (name != null) {
            properties.put("name", quote(name));
        }
        try {
            return new ObjectName(DOMAIN, properties);
        } catch (MalformedObjectNameException e) {
            logger.warn("ManagementService createObjectName error", e);
            throw new IllegalArgumentException();

        }
    }

    @Override
    public String toString() {
        return ManagementService.class.getName();
    }

}
