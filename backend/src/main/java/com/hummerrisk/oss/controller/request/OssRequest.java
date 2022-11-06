package com.hummerrisk.oss.controller.request;

import com.hummerrisk.commons.utils.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * harris
 */
public class OssRequest {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "云账号名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty("插件ID")
    private String pluginId;

    @ApiModelProperty("插件名称")
    private String pluginName;

    @ApiModelProperty(value = "状态", allowableValues = "VALID,INVALID,DELETED")
    private String status;

    private Map<String, Object> combine;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
