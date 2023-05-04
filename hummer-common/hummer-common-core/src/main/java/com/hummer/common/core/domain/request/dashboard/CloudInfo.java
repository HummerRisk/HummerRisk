package com.hummer.common.core.domain.request.dashboard;

import com.hummer.common.core.domain.Plugin;

import java.util.List;

/**
 * @ClassName CloudInfo
 * @Author harris
 **/
public class CloudInfo {

    private Integer clouds;

    private Integer accounts;

    private Integer resources;

    private List<Plugin> plugins;

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

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }
}
