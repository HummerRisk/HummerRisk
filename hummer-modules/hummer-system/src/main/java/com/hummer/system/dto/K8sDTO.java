package com.hummer.system.dto;

import com.hummer.common.core.domain.*;

import java.util.List;

public class K8sDTO {

    private CloudNativeResult cloudNativeResult;

    private List<CloudNativeResultItem> cloudNativeResultItemList;

    private List<CloudNativeResultConfigItem> cloudNativeResultConfigItemList;

    private List<CloudNativeResultKubenchWithBLOBs> cloudNativeResultKubenchList;

    private List<CloudTask> cloudTaskList;

    public CloudNativeResult getCloudNativeResult() {
        return cloudNativeResult;
    }

    public void setCloudNativeResult(CloudNativeResult cloudNativeResult) {
        this.cloudNativeResult = cloudNativeResult;
    }

    public List<CloudNativeResultItem> getCloudNativeResultItemList() {
        return cloudNativeResultItemList;
    }

    public void setCloudNativeResultItemList(List<CloudNativeResultItem> cloudNativeResultItemList) {
        this.cloudNativeResultItemList = cloudNativeResultItemList;
    }

    public List<CloudNativeResultConfigItem> getCloudNativeResultConfigItemList() {
        return cloudNativeResultConfigItemList;
    }

    public void setCloudNativeResultConfigItemList(List<CloudNativeResultConfigItem> cloudNativeResultConfigItemList) {
        this.cloudNativeResultConfigItemList = cloudNativeResultConfigItemList;
    }

    public List<CloudNativeResultKubenchWithBLOBs> getCloudNativeResultKubenchList() {
        return cloudNativeResultKubenchList;
    }

    public void setCloudNativeResultKubenchList(List<CloudNativeResultKubenchWithBLOBs> cloudNativeResultKubenchList) {
        this.cloudNativeResultKubenchList = cloudNativeResultKubenchList;
    }

    public List<CloudTask> getCloudTaskList() {
        return cloudTaskList;
    }

    public void setCloudTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }
}
