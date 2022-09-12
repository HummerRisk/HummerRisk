package com.hummerrisk.dto;


import com.hummerrisk.base.domain.Proxy;

public class SshServerDTO {

    private String sshIp;

    private String sshUserName;

    private String sshPassword;

    private int sshPort;

    private String isPublicKey;

    private String publicKey;

    private Proxy proxy;

    public String getSshIp() {
        return sshIp;
    }

    public void setSshIp(String sshIp) {
        this.sshIp = sshIp;
    }

    public String getSshUserName() {
        return sshUserName;
    }

    public void setSshUserName(String sshUserName) {
        this.sshUserName = sshUserName;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public int getSshPort() {
        return sshPort;
    }

    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    public String getIsPublicKey() {
        return isPublicKey;
    }

    public void setIsPublicKey(String isPublicKey) {
        this.isPublicKey = isPublicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
