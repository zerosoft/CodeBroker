package com.codebroker.util;

import javassist.*;
import javassist.util.HotSwapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * class类热替换
 */
public class HotSwapClassUtil {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private HotSwapper hotSwapper;
    private String path;

    // public HotSwapClassUtil(PropertiesWrapper wrapper) throws IOException,
    // IllegalConnectorArgumentsException {
    // super();
    // int port = wrapper.getIntProperty(SystemEnvironment.RELOAD_PORT, 8000);
    // this.path = wrapper.getProperty(SystemEnvironment.RELOAD_PATH,
    // "./reload");
    // this.hotSwapper = new HotSwapper(port);
    // }
    //

    /**
     * 类替换
     *
     * @param fileName  文件名
     * @param clazzName 类名称
     */
    public void reload(String fileName, String clazzName) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = null;
        try {
            File file = new File(path + File.separator + fileName);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            hotSwapper.reload(clazzName, bytes);
        } catch (Exception e) {
            logger.error("reload class error", e);
        } finally {
            fileInputStream.close();
        }

    }

    /**
     * 单个类的方法替换
     *
     * @param clazz      类的全名
     * @param methodName 方法名
     * @param context    替换的方法内容
     */
    public void reloadMethod(String clazz, String methodName, String context)
            throws NotFoundException, CannotCompileException, IOException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(clazz);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        cm.setBody(context);
        hotSwapper.reload(clazz, cc.toBytecode());
    }

    public void reloadJar(String fileName) throws IOException {
        JarFile jarFile = new JarFile(path + File.separator + fileName);
        Enumeration<JarEntry> entries = jarFile.entries();
        InputStream input = null;
        BufferedInputStream fileInputStream = null;
        while (entries.hasMoreElements()) {
            try {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                if (jarEntry.getName().endsWith("package-info.class")) {
                    continue;
                }
                if (!jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                input = jarFile.getInputStream(jarEntry);
                long size = jarEntry.getSize();
                byte[] bs = new byte[(int) size];
                fileInputStream = new BufferedInputStream(input);
                fileInputStream.read(bs);
                String clazzName = jarEntry.getName().replaceAll("/", ".");
                // System.out.println(clazzName + " " + bs.length);
                hotSwapper.reload(clazzName, bs);
            } catch (Exception e) {
                logger.error("reloadJar error", e);
            } finally {
                input.close();
                fileInputStream.close();
            }
        }

    }

}
