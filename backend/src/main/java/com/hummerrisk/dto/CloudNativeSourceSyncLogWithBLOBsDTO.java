package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudNativeSourceSyncLogWithBLOBs;


public class CloudNativeSourceSyncLogWithBLOBsDTO extends CloudNativeSourceSyncLogWithBLOBs {

    private String k8sName;

    private String pluginIcon;

    public String getK8sName() {
        return k8sName;
    }

    public void setK8sName(String k8sName) {
        this.k8sName = k8sName;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }
}
