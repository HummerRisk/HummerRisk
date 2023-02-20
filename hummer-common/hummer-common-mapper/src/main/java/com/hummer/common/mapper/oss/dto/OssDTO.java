package com.hummer.common.mapper.oss.dto;


import com.hummer.common.mapper.domain.OssWithBLOBs;

public class OssDTO extends OssWithBLOBs {

    private String userName;

    private boolean isProxy;

    private String proxyIp;

    private String proxyPort;

    private String proxyName;

    private String proxyPassword;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
}
