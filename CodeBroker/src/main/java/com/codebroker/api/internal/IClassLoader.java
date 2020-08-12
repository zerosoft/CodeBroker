package com.codebroker.api.internal;

public interface IClassLoader {
    ClassLoader loadClasses(String[] paramArrayOfString, ClassLoader paramClassLoader)
            throws Exception;

}
