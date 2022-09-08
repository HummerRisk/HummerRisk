package com.hummerrisk.service.impl;

import com.hummerrisk.commons.exception.PluginException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ExecEngineFactoryImp {

    private Logger logger = LoggerFactory.getLogger("pluginLogger");

    private List<Object> providers = new ArrayList<>();

    public <T> T getCloudProvider(String name) {
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

    <T> T executePluginMethod(IProvider provider, String method, Class<T> resultType, Object... params) throws Exception {
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

}
