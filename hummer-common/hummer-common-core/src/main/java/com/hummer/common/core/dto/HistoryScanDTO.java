package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.HistoryScan;

import java.io.Serializable;

public class HistoryScanDTO extends HistoryScan implements Serializable {

    private String pluginId;

    private String pluginName;

    private String pluginIcon;

    private String accountName;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginIcon() {
        return pluginIcon;
    }

    public void setPluginIcon(String pluginIcon) {
        this.pluginIcon = pluginIcon;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
