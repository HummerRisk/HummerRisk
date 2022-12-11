package com.hummerrisk.proxy.jdcloud;

public class JDCloudCredential {
    private String AccessKey;
    private String SecretAccessKey;

    public String getAccessKey() {
        return AccessKey;
    }

    public void setAccessKey(String accessKey) {
        AccessKey = accessKey;
    }

    public String getSecretAccessKey() {
        return SecretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        SecretAccessKey = secretAccessKey;
    }
}
