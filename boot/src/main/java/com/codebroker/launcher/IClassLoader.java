package com.codebroker.launcher;

public interface IClassLoader {
	ClassLoader loadClasses(String[] paramArrayOfString, ClassLoader paramClassLoader) throws BootException;
}
