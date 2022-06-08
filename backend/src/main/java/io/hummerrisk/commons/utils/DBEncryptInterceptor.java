package io.hummerrisk.commons.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DBEncryptInterceptor
 * @Author harris
 * @Version 1.0
 **/
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class DBEncryptInterceptor {

    private List<EncryptConfig> encryptConfigList;

    private ConcurrentHashMap<String, Class> classMap = new ConcurrentHashMap<>();

    private enum Methods {
        encrypt, decrypt
    }

    private ConcurrentHashMap<String, Map<String, Map<String, EncryptConfig>>> encryptConfigMap = new ConcurrentHashMap<>();

    public Object intercept(Invocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        Object parameter = invocation.getArgs()[1];
        if (parameter != null && methodName.equals("update")) {
            invocation.getArgs()[1] = encrypt(parameter);
        }
        Object returnValue = invocation.proceed();
        Object result = returnValue;
        if (returnValue instanceof ArrayList<?>) {
            List<Object> list = new ArrayList<>();
            boolean isDecrypted = false;
            for (Object val : (ArrayList<?>) returnValue) {
                Object a = decrypt(val);
                if (a == val) {
                    break;
                } else {
                    isDecrypted = true;
                    list.add(a);
                }
            }
            if (isDecrypted) {
                result = list;
            }
        } else {
            result = decrypt(returnValue);
        }
        return result;
    }

    private Map<String, Map<String, EncryptConfig>> getConfig(Object p) {
        Map<String, Map<String, EncryptConfig>> result = new HashMap<>();
        if (p == null) {
            return null;
        }
        String pClassName = p.getClass().getName();
        if (encryptConfigMap.get(pClassName) != null) {
            return encryptConfigMap.get(pClassName);
        }
        Map<String, List<EncryptConfig>> m = new HashMap<>();
        for (EncryptConfig encryptConfig : encryptConfigList) {
            String className = encryptConfig.getModelName();
            String attrName = encryptConfig.getAttrName();
            if (StringUtils.isNotBlank(className)) {
                Class c = classMap.get(className);
                if (c == null) {
                    try {
                        c = Class.forName(className);
                        classMap.put(className, c);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                }
                if (c.isInstance(p)) {
                    if (result.get(attrName) == null) {
                        result.put(attrName, new HashMap<>());
                    }
                    if (StringUtils.isNotBlank(encryptConfig.getEncryptMethod())) {
                        result.get(attrName).put(Methods.encrypt.name(), encryptConfig);
                    }
                    if (StringUtils.isNotBlank(encryptConfig.getEncryptMethod())) {
                        result.get(attrName).put(Methods.decrypt.name(), encryptConfig);
                    }
                }
            }
        }
        encryptConfigMap.put(pClassName, result);
        return result;
    }

    private Object encrypt(Object obj) throws Throwable {
        if (obj instanceof Map) {
            Map paramMap = (Map) obj;
            for (Object key : paramMap.keySet()) {
                if (paramMap.get(key) != null) {
                    paramMap.put(key, encrypt(paramMap.get(key)));
                }
            }
            return paramMap;
        }
        Map<String, Map<String, EncryptConfig>> localEncryptConfigMap = getConfig(obj);
        if (MapUtils.isEmpty(localEncryptConfigMap)) {
            return obj;
        }
        Object newObject = obj.getClass().newInstance();
        BeanUtils.copyBean(newObject, obj);
        for (String attrName : localEncryptConfigMap.keySet()) {
            if (MapUtils.isEmpty(localEncryptConfigMap.get(attrName))) {
                continue;
            }
            EncryptConfig encryptConfig = localEncryptConfigMap.get(attrName).get(Methods.encrypt.name());
            if (encryptConfig == null || StringUtils.isBlank(encryptConfig.getEncryptClass())
                    || StringUtils.isBlank(encryptConfig.getEncryptMethod())) {
                continue;
            }
            Object fieldValue = BeanUtils.getFieldValueByName(encryptConfig.getAttrName(), newObject);
            if (fieldValue != null) {
                Class<?> encryptClazz = Class.forName(encryptConfig.getEncryptClass());
                Method method = encryptClazz.getMethod(encryptConfig.getEncryptMethod(), Object.class);
                Object encryptedValue = method.invoke(null, fieldValue);
                if (encryptedValue instanceof byte[]) {
                    BeanUtils.setFieldValueByName(newObject, encryptConfig.getAttrName(), encryptedValue, byte[].class);
                } else {
                    BeanUtils.setFieldValueByName(newObject, encryptConfig.getAttrName(), encryptedValue, fieldValue.getClass());
                }
            }
        }

        return newObject;
    }

    private Object decrypt(Object obj) throws Throwable {
        Map<String, Map<String, EncryptConfig>> localDecryptConfigMap = getConfig(obj);
        Object result;
        if (MapUtils.isEmpty(localDecryptConfigMap)) {
            return obj;
        }
        result = obj.getClass().newInstance();
        BeanUtils.copyBean(result, obj);
        for (String attrName : localDecryptConfigMap.keySet()) {
            if (MapUtils.isEmpty(localDecryptConfigMap.get(attrName))) {
                continue;
            }
            EncryptConfig encryptConfig = localDecryptConfigMap.get(attrName).get(Methods.decrypt.name());
            if (encryptConfig == null || StringUtils.isBlank(encryptConfig.getDecryptClass())
                    || StringUtils.isBlank(encryptConfig.getDecryptMethod())) {
                continue;
            }
            Object fieldValue = BeanUtils.getFieldValueByName(encryptConfig.getAttrName(), result);
            if (fieldValue != null) {
                Class<?> encryptClazz = Class.forName(encryptConfig.getDecryptClass());
                Object decryptedValue;
                if (fieldValue instanceof List) {
                    Method method = encryptClazz.getMethod(encryptConfig.getDecryptMethod(), List.class, String.class);
                    //fieldValue获取的是list的引用，所以list类型的属性不需要再调用setFieldValueByName了
                    method.invoke(null, fieldValue, encryptConfig.getAttrNameForList());
                } else {
                    Method method = encryptClazz.getMethod(encryptConfig.getDecryptMethod(), Object.class);
                    decryptedValue = method.invoke(null, fieldValue);
                    if (decryptedValue instanceof byte[]) {
                        BeanUtils.setFieldValueByName(result, encryptConfig.getAttrName(), decryptedValue, byte[].class);
                    } else {
                        BeanUtils.setFieldValueByName(result, encryptConfig.getAttrName(), decryptedValue, fieldValue.getClass());
                    }
                }
            }
        }
        return result;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, (Interceptor) this);
    }

    public void setProperties(Properties properties) {

    }

    public List<EncryptConfig> getEncryptConfigList() {
        return encryptConfigList;
    }

    public void setEncryptConfigList(List<EncryptConfig> encryptConfigList) {
        this.encryptConfigList = encryptConfigList;
    }

}
