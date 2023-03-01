package com.hummer.cloud.oss.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class BucketMetric implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("存储名称")
    private String bucketName;

    @ApiModelProperty("存储ID")
    private Long bucketId;

    @ApiModelProperty("同步时间")
    private Long syncTime;

    @ApiModelProperty("")
    private String dayFormat;

    @ApiModelProperty("")
    private String weekFormat;

    @ApiModelProperty("")
    private String monthFormat;

    @ApiModelProperty("")
    private String hourFormat;

    @ApiModelProperty("存储用量(M)")
    private Long size;

    @ApiModelProperty("文件数量")
    private Long objectNumber;

    @ApiModelProperty("工作空间")
    private String workspaceId;

    @ApiModelProperty("云账号ID")
    private String credentialId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getBucketId() {
        return bucketId;
    }

    public void setBucketId(Long bucketId) {
        this.bucketId = bucketId;
    }

    public Long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Long syncTime) {
        this.syncTime = syncTime;
    }

    public String getDayFormat() {
        return dayFormat;
    }

    public void setDayFormat(String dayFormat) {
        this.dayFormat = dayFormat;
    }

    public String getWeekFormat() {
        return weekFormat;
    }

    public void setWeekFormat(String weekFormat) {
        this.weekFormat = weekFormat;
    }

    public String getMonthFormat() {
        return monthFormat;
    }

    public void setMonthFormat(String monthFormat) {
        this.monthFormat = monthFormat;
    }

    public String getHourFormat() {
        return hourFormat;
    }

    public void setHourFormat(String hourFormat) {
        this.hourFormat = hourFormat;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getObjectNumber() {
        return objectNumber;
    }

    public void setObjectNumber(Long objectNumber) {
        this.objectNumber = objectNumber;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }
}
