package com.codebroker.api.internal;

/**
 * 自定义类加载器
 */
public interface IClassLoader {
    ClassLoader loadClasses(String[] jarPath, ClassLoader classLoader)
            throws Exception;

}
