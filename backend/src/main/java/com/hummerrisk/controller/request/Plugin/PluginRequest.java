package com.hummerrisk.controller.request.Plugin;

import io.swagger.annotations.ApiModelProperty;

public class PluginRequest {

    @ApiModelProperty("插件ID")
    private String id;

    @ApiModelProperty("插件名称")
    private String name;

    @ApiModelProperty("插件描述,模糊匹配")
    private String description;

    @ApiModelProperty(value = "排序key", hidden = true)
    private String sort;

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
}
