package com.codebroker.launcher;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

public final class Boot {
	private static final String version = "1.0.2";

	private static final String KEY_BOOT_CLASS_NAME = "bootClassName";

	private static final String KEY_LIB_FOLDERS = "libFolders";

	private static final String KEY_DEBUG = "debug";

	private static String CFG_FILE_NAME = "boot.properties";

	private static String FOLDER_SEPARATOR = ",";

	private String[] libFolders;

	private String bootClassName;

	private static boolean debug;

	private ClassLoader bootClassLoader;

	private final IClassLoader jarLoader = new JarLoader();

	public Boot(String[] args) {
		try {
			loadConfiguration();
			loadDependencies();
			startMain(args);
		} catch (BootException err) {
			System.out.println(err);
		} catch (IOException err) {
			System.out.println("I/O Error loading the boot configuration file: " + err);
			if (isDebug())
				err.printStackTrace();
		} catch (Exception err) {
			System.out.println("Unexpected error at boot time: " + err);
			if (isDebug())
				err.printStackTrace();
		}
	}

	public static boolean isDebug() {
		return debug;
	}

	private void loadConfiguration() throws IOException, BootException {
		Properties config = new Properties();
		config.load(new FileInputStream(CFG_FILE_NAME));
		this.bootClassName = config.getProperty("bootClassName");
		String folders = config.getProperty("libFolders");
		debug = !(config.getProperty("debug") == null);
		if (this.bootClassName == null || this.bootClassName.length() == 0)
			throw new BootException("Boot Main Class was not provided! Booting aborted.");
		if (folders == null || folders.length() == 0)
			throw new BootException("No Library Folders provided! Booting aborted.");
		if (debug)
			signature();
		this.libFolders = folders.split("\\" + FOLDER_SEPARATOR);
	}

	private void loadDependencies() throws BootException {
		this.bootClassLoader = this.jarLoader.loadClasses(this.libFolders, Boot.class.getClassLoader());
	}

	private void startMain(String[] args) throws BootException {
		Thread.currentThread().setContextClassLoader(this.bootClassLoader);
		try {
			Class<?> mainClass = this.bootClassLoader.loadClass(this.bootClassName);
			Method mainMethod = mainClass.getMethod("main", new Class[] { String[].class });
			if (debug)
				System.out.println("Launching Main with args: " + Arrays.toString((Object[])args));
			mainMethod.invoke(null, new Object[] { args });
		} catch (SecurityException e) {
			throw new BootException("Error running main(String[] args) method on Boot Class: " + e);
		} catch (NoSuchMethodException e) {
			throw new BootException("No main(String[] args) method in Boot Class " + e);
		} catch (Exception e) {
			if (isDebug())
				e.printStackTrace();
			throw new BootException("Unexpected error: " + e);
		}
	}

	private void signature() {
		System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
		System.out.println("   AppLauncher - 1.0.2 - (c) 2009 gotoAndply()");
		System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::");
	}

	public static void main(String[] args) {
		if (args.length > 0)
			CFG_FILE_NAME = args[0];
		if (args.length > 1) {
			args = Arrays.<String>copyOfRange(args, 1, args.length);
		} else {
			args = new String[0];
		}
	}
}
