package com.hummer.common.core.domain.request.dashboard;

import com.hummer.common.core.dto.PluginDTO;

import java.util.List;

/**
 * @ClassName CloudInfo
 * @Author harris
 **/
public class CloudInfo {

    private Integer clouds;

    private Integer accounts;

    private Integer resources;

    private List<PluginDTO> plugins;

    public Integer getClouds() {
        return clouds;
    }

    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    public Integer getAccounts() {
        return accounts;
    }

    public void setAccounts(Integer accounts) {
        this.accounts = accounts;
    }

    public Integer getResources() {
        return resources;
    }

    public void setResources(Integer resources) {
        this.resources = resources;
    }

    public List<PluginDTO> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginDTO> plugins) {
        this.plugins = plugins;
    }
}
