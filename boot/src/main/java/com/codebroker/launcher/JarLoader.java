package com.codebroker.launcher;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class JarLoader implements IClassLoader {
	public ClassLoader loadClasses(String[] paths, ClassLoader parentClassLoader) throws BootException {
		ClassLoader jarClassLoader;
		List<URL> locations = new ArrayList<>();
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = paths).length, b = 0; b < i; ) {
			String folder = arrayOfString[b];
			List<String> jarFiles = JarFilesUtil.scanFolderForJarFiles(folder);
			for (String jarFilePath : jarFiles) {
				try {
					File jarFile = new File(jarFilePath);
					locations.add(jarFile.toURI().toURL());
				} catch (MalformedURLException e) {
					throw new BootException("Malformed URL: " + jarFilePath);
				}
			}
			if (locations.size() == 0)
				throw new BootException("Unexpected: no jars were located!");
			b++;
		}
		URL[] classPath = new URL[locations.size()];
		locations.toArray(classPath);
		if (Boot.isDebug()) {
			URL[] arrayOfURL;
			for (int j = (arrayOfURL = classPath).length; i < j; ) {
				URL item = arrayOfURL[i];
				System.out.println("Adding to classpath: " + item);
				i++;
			}
		}
		jarClassLoader = new URLClassLoader(classPath, parentClassLoader);
		return jarClassLoader;
	}
}