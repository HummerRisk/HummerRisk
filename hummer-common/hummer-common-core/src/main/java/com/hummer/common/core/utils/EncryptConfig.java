package com.hummer.common.core.utils;

/**
 * @ClassName EncryptConfig
 * @Author harris
 * @Version 1.0
 **/
public class EncryptConfig {

    private String modelName;
    private String attrName;
    private String attrNameForList;
    private String encryptClass;
    private String encryptMethod;
    private String decryptClass;
    private String decryptMethod;


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

    public String getEncryptMethod() {
        return encryptMethod;
    }

    public void setEncryptMethod(String encryptMethod) {
        this.encryptMethod = encryptMethod;
    }

    public String getDecryptMethod() {
        return decryptMethod;
    }

    public void setDecryptMethod(String decryptMethod) {
        this.decryptMethod = decryptMethod;
    }

    public String getEncryptClass() {
        return encryptClass;
    }

    public void setEncryptClass(String encryptClass) {
        this.encryptClass = encryptClass;
    }

    public String getDecryptClass() {
        return decryptClass;
    }

    public void setDecryptClass(String decryptClass) {
        this.decryptClass = decryptClass;
    }

    public EncryptConfig() {
    }

    /**
     * 用时需谨慎！！！！！
     * 主要配置多个的时候，参数少一点
     *
     * @param modelName
     * @param attrName
     */
    public EncryptConfig(String modelName, String attrName) {
        this.modelName = modelName;
        this.attrName = attrName;
        this.encryptClass = "EncryptUtils";
        this.encryptMethod = "aesEncrypt";
        this.decryptClass = "EncryptUtils";
        this.decryptMethod = "aesDecrypt";
    }

    public EncryptConfig(String modelName, String attrName, String attrNameForList) {
        this.modelName = modelName;
        this.attrName = attrName;
        this.attrNameForList = attrNameForList;
        this.encryptClass = "EncryptUtils";
        this.encryptMethod = "aesEncrypt";
        this.decryptClass = "EncryptUtils";
        this.decryptMethod = "aesDecrypt";
    }

    public EncryptConfig(String modelName, String attrName, String encryptClass, String encryptMethod, String decryptMethod) {
        this.modelName = modelName;
        this.attrName = attrName;
        this.encryptClass = encryptClass;
        this.encryptMethod = encryptMethod;
        this.decryptClass = encryptClass;
        this.decryptMethod = decryptMethod;
    }

}
