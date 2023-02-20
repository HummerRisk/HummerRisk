package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.Image;

public class ImageDTO extends Image {

    private String userName;

    private String proxyIp;

    private String proxyPort;

    private String proxyName;

    private String proxyPassword;

    private String user;

    private String imageRepoName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImageRepoName() {
        return imageRepoName;
    }

    public void setImageRepoName(String imageRepoName) {
        this.imageRepoName = imageRepoName;
    }
}
