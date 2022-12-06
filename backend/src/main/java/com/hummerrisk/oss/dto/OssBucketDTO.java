package com.hummerrisk.oss.dto;

import com.hummerrisk.base.domain.OssBucket;


public class OssBucketDTO extends OssBucket {

    private String name;

    private String pluginId;

    private String pluginIcon;

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
}
