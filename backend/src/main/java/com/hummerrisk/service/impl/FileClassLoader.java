package com.hummerrisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class FileClassLoader extends URLClassLoader {

    public FileClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    public void addFile(File f) throws IOException {
        addFile(f.toURI().toURL());
    }

    public void addFile(URL u) throws IOException {
        try {
            this.addURL(u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
}
