package com.codebroker.api.internal;

import jodd.io.findfile.FindFile;
import jodd.util.cl.ExtendedURLClassLoader;
import org.apache.tools.ant.taskdefs.Classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * jar读取器
 */
public class JarLoader implements IClassLoader{

    private ExtendedURLClassLoader extendedURLClassLoader;

    @Override
    public ClassLoader loadClasses(String[] jarPath, ClassLoader classLoader) throws Exception {
        FindFile ff = new FindFile()
                .recursive(true)
                .includeDirs(true)
                .searchPath(jarPath);
        List<File> all = ff.findAll();
        //找到的jar转换成数组
        URL[] classpath=new URL[all.size()];
        for (int i = 0; i < all.size(); i++) {
            classpath[i] = all.get(i).toURI().toURL();
        }
        extendedURLClassLoader=new ExtendedURLClassLoader(classpath, classLoader,true);
        return extendedURLClassLoader;
    }
}
