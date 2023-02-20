package com.hummer.common.mapper.domain.request.cloudNative;

import io.swagger.annotations.ApiModelProperty;

/**
 * harris
 */
public class CreateCloudNativeRequest {

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
}
