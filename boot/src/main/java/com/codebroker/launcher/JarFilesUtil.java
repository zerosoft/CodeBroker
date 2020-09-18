package com.codebroker.launcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JarFilesUtil {
	private static final String JAR_EXT = ".jar";

	public static List<String> scanFolderForJarFiles(String path) throws BootException {
		List<String> jarFiles = new ArrayList<String>();
		File theFolder = new File(path);
		if (!theFolder.isDirectory())
			throw new BootException("The provided path is not a directory: " + path);
		byte b;
		int i;
		File[] arrayOfFile;
		for (i = (arrayOfFile = theFolder.listFiles()).length, b = 0; b < i; ) {
			File fileEntry = arrayOfFile[b];
			if (fileEntry.isFile()) {
				String fileName = fileEntry.getName();
				if (hasExtension(fileName, ".jar"))
					jarFiles.add(String.valueOf(path) + "/" + fileName);
			}
			b++;
		}
		return jarFiles;
	}

	public static List<String> scanClassNamesInJarFile(String jarFilePath) throws BootException {
		List<String> classNames = new ArrayList<String>();
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.isDirectory())
					if (entry.getName().endsWith(".class")) {
						String fqcName = entry.getName().replace('/', '.');
						classNames.add(fqcName.substring(0, fqcName.length() - 6));
					}
			}
		} catch (IOException e) {
			throw new BootException("Cannot access jar file: " + jarFilePath);
		}
		return classNames;
	}

	private static boolean hasExtension(String fileName, String expectedExtension) {
		boolean isOk = false;
		if (fileName == null)
			return isOk;
		int extPos = fileName.lastIndexOf('.');
		if (extPos > 0) {
			String fileExt = fileName.substring(extPos);
			if (expectedExtension.equalsIgnoreCase(fileExt))
				isOk = true;
		}
		return isOk;
	}
}
