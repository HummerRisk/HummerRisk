package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudNativeSourceWithBLOBs;

public class CloudNativeSourceDTO extends CloudNativeSourceWithBLOBs {

    private String userName;

    private String cloudNativeName;

    private String pluginIcon;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCloudNativeName() {
        return cloudNativeName;
    }

    public void setCloudNativeName(String cloudNativeName) {
        this.cloudNativeName = cloudNativeName;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }
}
