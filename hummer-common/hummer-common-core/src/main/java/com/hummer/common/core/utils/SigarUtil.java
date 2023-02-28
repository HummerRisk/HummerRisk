package com.hummer.common.core.utils;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;

public class SigarUtil {

    private static Logger logger = LoggerFactory.getLogger(SigarUtil.class);

    static {

        // Linux MacOS 分隔符 : Windows 是;
        String osName = System.getProperty("os.name", "generic").toLowerCase();
        String splitSymbol = osName.contains("win") ? ";" : ":";

        // 寻找 classpath 根目录下的 sigar 文件夹
        URL sigarURL = SigarUtil.class.getResource("/sigar");
        if (null == sigarURL) {
            // 找不到抛异常
            throw new MissingResourceException("miss classpath:/sigar folder", SigarUtil.class.getName(), "classpath:/sigar");
        }

        File classPath = new File(sigarURL.getFile());
        String oldLibPath = System.getProperty("java.library.path");

        try {
            // 追加库路径
            String newLibPath = oldLibPath + splitSymbol + classPath.getCanonicalPath();
            System.setProperty("java.library.path", newLibPath);

            logger.info("set sigar java.library.path={}", newLibPath);

        } catch (IOException e) {
            logger.error("append sigar to java.library.path error", e);
        }
    }

    private static class SingleSigar {
        private static final Sigar SIGAR = new Sigar();
    }

    public static Sigar getInstance() {
        return SingleSigar.SIGAR;
    }


}
