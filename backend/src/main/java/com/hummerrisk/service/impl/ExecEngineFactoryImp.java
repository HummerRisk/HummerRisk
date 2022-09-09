package com.hummerrisk.service.impl;

import com.hummerrisk.commons.exception.PluginException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class ExecEngineFactoryImp {

    //获取当前文件所在的路径
    private String localPath = this.getClass().getResource("").getPath();

    private String targetFilePath = localPath.replace("listener/", "") + "service/impl/provider";

    private Logger logger = LoggerFactory.getLogger("pluginLogger");

    public List<Object> providers = new ArrayList<>();

    private String pluginBasePackage = "com.hummerrisk.service.impl";

    private Class<? extends Annotation> pluginAnnotationType = HummerPlugin.class;

    private ListableBeanFactory context;

    public FileClassLoader getFileClassLoader() {
        return fileClassLoader;
    }

    private FileClassLoader fileClassLoader;

    public void init() {
        loadContext();
    }

    public <T> T getProvider(String name) {
        for (Object cp : providers) {
            try {
                Method getName = cp.getClass().getMethod("getName");
                Object invoke = getName.invoke(cp);
                if (invoke != null && invoke.toString().equals(name)) {
                    return (T) cp;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object executeMethod(IProvider provider, String method, Object... params) throws Exception {
        return executeMethod(provider, method, Object.class, params);
    }

    <T> T executeMethod(IProvider provider, String method, Class<T> resultType, Object... params) throws Exception {
        String requestId = UUID.randomUUID().toString();
        try {
            Object result = MethodUtils.invokeMethod(provider, method, params);
            return resultType.cast(result);
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof PluginException) {
                throw (PluginException) targetException;
            }
        } catch (Exception e) {
            logger.error("RequestId: {}, Method: {}", requestId, method, e);
            throw e;
        }
        return null;
    }

    private void loadContext() {
        if (context == null) {
            // Create a parent context containing all beans provided to plugins
            // More on that below in the article...
            GenericApplicationContext parentContext = new GenericApplicationContext();
            parentContext.refresh();

            // Create the annotation-based context
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.setParent(parentContext);

            // Scan for classes annotated with @<PluginAnnotaionType>,
            // do not include standard Spring annotations in scan
            ClassPathBeanDefinitionScanner scanner =
                    new ClassPathBeanDefinitionScanner(context, false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(pluginAnnotationType));
            scanner.scan(pluginBasePackage);
            context.refresh();
            this.context = context;
        }

    }

    private Collection getPluginDescriptors(ListableBeanFactory context) {
        return context.getBeansWithAnnotation(pluginAnnotationType).values();
    }

    protected Collection getPlugins() {
        try {
            Thread currentThread = Thread.currentThread();
            ClassLoader sysCloader = currentThread.getContextClassLoader();
            fileClassLoader = new FileClassLoader(new URL[]{new File(targetFilePath).toURI().toURL()}, sysCloader);
            currentThread.setContextClassLoader(fileClassLoader);
            scanFileToPath(targetFilePath, 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        loadContext();
        return getPluginDescriptors(context);
    }

    private void scanFileToPath(String filePath, int innerLevel) {
        File file = new File(filePath);
        // get the folder list
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                String tmpPath = tmp.getPath();
                int level = innerLevel - 1;
                if (level > 0) {
                    if (tmp.isDirectory()) {
                        scanFileToPath(tmpPath, level);
                    }
                }

                if (tmp.getName().endsWith(".java")) {
                    try {
                        fileClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
