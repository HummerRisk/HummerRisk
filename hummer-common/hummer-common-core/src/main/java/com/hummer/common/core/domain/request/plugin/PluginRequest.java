package com.hummer.common.core.domain.request.plugin;

import com.hummerrisk.base.domain.Plugin;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class PluginRequest extends Plugin {

    @ApiModelProperty("插件ID")
    private String id;

    @ApiModelProperty("插件名称")
    private String name;

    @ApiModelProperty("插件描述,模糊匹配")
    private String description;

    @ApiModelProperty(value = "排序key", hidden = true)
    private String sort;

    private Map<String, Object> combine;

    private String pluginId;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSort() {
        return sort;
    }


    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
}
