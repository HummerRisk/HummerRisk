package com.hummer.common.core.domain.request.config;

import com.hummerrisk.commons.utils.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * harris
 */
public class ConfigRequest {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "云原生部署配置名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty(value = "状态", allowableValues = "VALID,INVALID,DELETED")
    private String status;

    @ApiModelProperty(value = "排序key", hidden = true)
    private String sort;

    @ApiModelProperty("创建时间")
    private Integer createTimeStart;

    @ApiModelProperty("创建时间")
    private Integer createTimeEnd;

    @ApiModelProperty("更新时间")
    private Integer updateTimeStart;

    @ApiModelProperty("更新时间")
    private Integer updateTimeEnd;

    private String userName;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Integer createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Integer getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Integer createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(Integer updateTimeStart) {
        this.updateTimeStart = updateTimeStart;
    }

    public Integer getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(Integer updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
