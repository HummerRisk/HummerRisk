package com.hummer.common.core.dto;

import com.hummer.common.core.domain.AccountWithBLOBs;
import com.hummer.common.core.domain.CloudEventSyncLog;
import com.hummer.common.core.domain.ImageRepo;
import com.hummer.common.core.domain.Oss;

import java.util.List;

public class AccountDTO extends AccountWithBLOBs {

    private String userName;

    private boolean isProxy;

    private String proxyIp;

    private String proxyPort;

    private String proxyName;

    private String proxyPassword;

    private List<Oss> ossList;

    private List<CloudEventSyncLog> cloudEventSyncLogList;

    private List<ImageRepo> imageRepoList;

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

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean proxy) {
        isProxy = proxy;
    }

    public List<Oss> getOssList() {
        return ossList;
    }

    public void setOssList(List<Oss> ossList) {
        this.ossList = ossList;
    }

    public List<CloudEventSyncLog> getCloudEventSyncLogList() {
        return cloudEventSyncLogList;
    }

    public void setCloudEventSyncLogList(List<CloudEventSyncLog> cloudEventSyncLogList) {
        this.cloudEventSyncLogList = cloudEventSyncLogList;
    }

    public List<ImageRepo> getImageRepoList() {
        return imageRepoList;
    }

    public void setImageRepoList(List<ImageRepo> imageRepoList) {
        this.imageRepoList = imageRepoList;
    }
}
