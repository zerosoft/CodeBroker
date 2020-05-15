package com.codebroker.util;

import com.codebroker.exception.FileNotExitException;
import com.google.common.collect.Lists;
import jodd.io.findfile.FindFile;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;

/**
 * The Class FileUtil.
 */
public final class FileUtil {

    /**
     * 查找文件.
     *
     * @param searchPath  搜索路径
     * @param IncludeDirs 是否包含子文件夹
     * @return the list
     */
    public static List<File> scanPath(String searchPath, boolean IncludeDirs) {
        List<File> result = Lists.newArrayList();
        FindFile findFile = new FindFile();
        FindFile ff = findFile.searchPath(searchPath);
        File f;
        while ((f = ff.nextFile()) != null) {
            result.add(f);
        }
        return result;
    }

    /**
     * 查找文件(不包含子文件夹).
     *
     * @param searchPath 搜索路径
     * @return the list
     */
    public static List<File> scanPath(String searchPath) {
        return scanPath(searchPath, false);
    }

    /**
     * 查找文件在指定目录下.
     *
     * @param searchPath the search path
     * @param fileName   the file name
     * @return the file
     * @throws FileNotExitException the file not exit exception
     */
    public static File scanFileByPath(String searchPath, String fileName) throws FileNotExitException {
        List<File> scanPath = scanPath(searchPath, false);
        for (File file : scanPath) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        throw new FileNotExitException();
    }

    public static String splitFilePath(String fullName) {
        String path = "";
        fullName.replace("\\", "/");
        String[] items = fullName.split("/");
        for (int i = 0; i < items.length - 1; i++) {
            path = path + items[i];
            path = path + "/";
        }
        return path;
    }

    public static boolean existFile(String fileName) {
        File file = new File(fileName);
        return (file.exists()) && (file.isFile());
    }

    public static boolean existFolder(String folderName) {
        File file = new File(folderName);
        return (file.exists()) && (file.isDirectory());
    }

    public static long getFileSize(String fileName) {
        try {
            RandomAccessFile rafFile = new RandomAccessFile(fileName, "rb");
            long size = rafFile.length();
            rafFile.close();
            return size;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static boolean osDeleteFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    public static boolean renameFile(String fileName, String newFileName) {
        File file = new File(fileName);
        return file.renameTo(new File(newFileName));
    }

    public static boolean setFileWritable(String fileName) {
        File file = new File(fileName);
        return file.setWritable(true);
    }

    public static boolean createDir(String dirName) {
        if (dirName.length() <= 0) {
            return true;
        }
        String sysDir = dirName;
        sysDir.replace("\\", "/");
        File file = new File(dirName);
        return file.mkdirs();
    }

    public static void makeSureFilePath(String filePath) {
        createDir(filePath);
    }

    public static void makeSureFileName(String fileName) {
        String folderPath = splitFilePath(fileName);
        createDir(folderPath);
    }

    public static String readFile(String filePath) throws Exception {
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        try {
            InputStream inputStream = new FileInputStream(filePath);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);

            String line = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            bufferReader.close();
            inputReader.close();
        }
    }

    public static long getProcessId() {
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            return Long.parseLong(processName.split("@")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long getThreadId() {
        long threadId = Thread.currentThread().getId();
        return threadId;
    }

    public static void printOsEnv() {
        Properties props = System.getProperties();
        LogUtil.logPrintln("Os: " + props.getProperty("os.name") + ", Arch: " + props.getProperty("os.arch") + ", Version: " + props.getProperty("os.version"));

        String userDir = System.getProperty("user.dir");
        LogUtil.logPrintln("UserDir: " + userDir);

        String homeDir = System.getProperty("java.home");
        LogUtil.logPrintln("JavaHome: " + homeDir);
    }

    public static boolean isWindowsOS() {
        Properties props = System.getProperties();
        if (props.getProperty("os.name").toLowerCase().contains("windows")) {
            return true;
        }
        return false;
    }
}
