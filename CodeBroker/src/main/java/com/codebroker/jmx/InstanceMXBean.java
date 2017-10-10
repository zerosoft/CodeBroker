package com.codebroker.jmx;

import java.io.FileNotFoundException;
import java.io.IOException;

import javassist.CannotCompileException;
import javassist.NotFoundException;

public interface InstanceMXBean {

	String getName();

	void stopEngine();

	void reloadJar(String fileName) throws FileNotFoundException, IOException;

	void reloadClazz(String fileName, String clazzName) throws FileNotFoundException, IOException;

	void reloadMethod(String clazz, String methodName, String context)
			throws NotFoundException, CannotCompileException, IOException;
}
