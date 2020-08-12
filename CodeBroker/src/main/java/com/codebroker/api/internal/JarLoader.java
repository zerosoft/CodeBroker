package com.codebroker.api.internal;

import jodd.io.findfile.FindFile;
import jodd.util.cl.ExtendedURLClassLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JarLoader implements IClassLoader{

    private ExtendedURLClassLoader extendedURLClassLoader;

    @Override
    public ClassLoader loadClasses(String[] paramArrayOfString, ClassLoader paramClassLoader) throws Exception {
        FindFile ff = new FindFile()
                .recursive(true)
                .includeDirs(true)
                .searchPath(paramArrayOfString);
        List<File> all = ff.findAll();
        URL[] classpath=new URL[all.size()];
        for (int i = 0; i < all.size(); i++) {
            classpath[i]=all.get(i).toURI().toURL();
        }
        this.extendedURLClassLoader=new ExtendedURLClassLoader(classpath,ClassLoader.getSystemClassLoader(),true);
        return extendedURLClassLoader;
    }
}
