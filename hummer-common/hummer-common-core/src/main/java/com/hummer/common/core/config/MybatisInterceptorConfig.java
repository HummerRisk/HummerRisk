package com.hummer.common.core.config;

import com.hummer.common.core.utils.EncryptUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MybatisInterceptorConfig {
    private String modelName;
    private String attrName;
    private String attrNameForList;
    private String interceptorClass;
    private String interceptorMethod;
    private String undoClass;
    private String undoMethod;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrNameForList() {
        return attrNameForList;
    }

    public void setAttrNameForList(String attrNameForList) {
        this.attrNameForList = attrNameForList;
    }

    public String getInterceptorClass() {
        return interceptorClass;
    }

    public void setInterceptorClass(String interceptorClass) {
        this.interceptorClass = interceptorClass;
    }

    public String getInterceptorMethod() {
        return interceptorMethod;
    }

    public void setInterceptorMethod(String interceptorMethod) {
        this.interceptorMethod = interceptorMethod;
    }

    public String getUndoClass() {
        return undoClass;
    }

    public void setUndoClass(String undoClass) {
        this.undoClass = undoClass;
    }

    public String getUndoMethod() {
        return undoMethod;
    }

    public void setUndoMethod(String undoMethod) {
        this.undoMethod = undoMethod;
    }

    public MybatisInterceptorConfig() {
    }

    /**
     * 用时需谨慎！！！！！
     * 主要配置多个的时候，参数少一点
     *
     * @param modelClass
     * @param attrName
     */
    public MybatisInterceptorConfig(Class<?> modelClass, String attrName) {
        this.modelName = modelClass.getName();
        this.attrName = attrName;
        this.interceptorClass = EncryptUtils.class.getName();
        this.interceptorMethod = "aesEncrypt";
        this.undoClass = EncryptUtils.class.getName();
        this.undoMethod = "aesDecrypt";
    }

    public MybatisInterceptorConfig(Class<?> modelClass, String attrName, Class<?> interceptorClass, String interceptorMethod, String undoMethod) {
        this.modelName = modelClass.getName();
        this.attrName = attrName;
        this.interceptorClass = interceptorClass.getName();
        this.interceptorMethod = interceptorMethod;
        this.undoClass = interceptorClass.getName();
        this.undoMethod = undoMethod;
    }

}
