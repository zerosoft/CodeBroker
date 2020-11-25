package com.codebroker.api.classloader;

/**
 * 自定义类加载器
 */
public interface IClassLoader {
    ClassLoader loadClasses(String jarPath, ClassLoader classLoader)
            throws Exception;

}
