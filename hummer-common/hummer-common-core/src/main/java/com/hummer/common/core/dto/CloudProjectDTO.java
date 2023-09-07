package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudProject;
import com.hummer.common.core.domain.CloudProjectLog;

import java.util.List;

public class CloudProjectDTO extends CloudProject {

    private List<CloudProjectLog> cloudProjectLogList;

    private List<CloudGroupDTO> cloudGroupList;

    private Integer groups;

    private Integer rules;

    private Integer riskGroups;

    private Integer riskRules;

    private Boolean isLatest;

    public List<CloudProjectLog> getCloudProjectLogList() {
        return cloudProjectLogList;
    }

    public void setCloudProjectLogList(List<CloudProjectLog> cloudProjectLogList) {
        this.cloudProjectLogList = cloudProjectLogList;
    }

    public List<CloudGroupDTO> getCloudGroupList() {
        return cloudGroupList;
    }

    public void setCloudGroupList(List<CloudGroupDTO> cloudGroupList) {
        this.cloudGroupList = cloudGroupList;
    }

    public Integer getGroups() {
        return groups;
    }

    public void setGroups(Integer groups) {
        this.groups = groups;
    }

    public Integer getRules() {
        return rules;
    }

    public void setRules(Integer rules) {
        this.rules = rules;
    }

    public Integer getRiskGroups() {
        return riskGroups;
    }

    public void setRiskGroups(Integer riskGroups) {
        this.riskGroups = riskGroups;
    }

    public Integer getRiskRules() {
        return riskRules;
    }

    public void setRiskRules(Integer riskRules) {
        this.riskRules = riskRules;
    }

    public Boolean getLatest() {
        return isLatest;
    }

    public void setLatest(Boolean latest) {
        isLatest = latest;
    }
}
