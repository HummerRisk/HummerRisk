package com.hummerrisk.controller.request.cloudTask;

import io.swagger.annotations.ApiModelProperty;

public class CloudTaskSearchRequest {
    @ApiModelProperty("申请人")
    private String applyUser;
    @ApiModelProperty("任务号")
    private String id;
    @ApiModelProperty("任务状态")
    private String status;
    @ApiModelProperty("流程 ID")
    private String processId;
    @ApiModelProperty("资源名称")
    private String cloudServerName;
    @ApiModelProperty("任务类型")
    private String type;
    @ApiModelProperty("任务描述")
    private String description;
    @ApiModelProperty("申请时间 Start")
    private Long createTimeStart;
    @ApiModelProperty("申请时间 End")
    private Long createTimeEnd;

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getCloudServerName() {
        return cloudServerName;
    }

    public void setCloudServerName(String cloudServerName) {
        this.cloudServerName = cloudServerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Long createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Long getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Long createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
