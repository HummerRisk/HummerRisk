package com.hummer.common.core.domain.request.account;

import io.swagger.annotations.ApiModelProperty;

/**
 * harris
 */
public class CreateCloudAccountRequest {

    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "插件ID", required = true)
    private String pluginId;
    @ApiModelProperty(value = "插件图标", required = true)
    private String pluginIcon;
    @ApiModelProperty(value = "凭据", required = true)
    private String credential;
    @ApiModelProperty(value = "代理", required = true)
    private int proxyId;
    @ApiModelProperty(value = "同时创建操作审计账号", required = true)
    private boolean createLog;
    @ApiModelProperty(value = "同时创建对象存储账号", required = true)
    private boolean createOss;
    @ApiModelProperty(value = "同时创建镜像仓库", required = true)
    private boolean createImage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    public boolean isCreateLog() {
        return createLog;
    }

    public void setCreateLog(boolean createLog) {
        this.createLog = createLog;
    }

    public boolean isCreateOss() {
        return createOss;
    }

    public void setCreateOss(boolean createOss) {
        this.createOss = createOss;
    }

    public boolean isCreateImage() {
        return createImage;
    }

    public void setCreateImage(boolean createImage) {
        this.createImage = createImage;
    }
}
