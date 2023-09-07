package com.hummer.system.dto;

import com.hummer.common.core.domain.CloudNativeConfigResult;
import com.hummer.common.core.domain.CloudNativeConfigResultItem;

import java.util.List;

public class ConfigDTO {

    private CloudNativeConfigResult cloudNativeConfigResult;

    private List<CloudNativeConfigResultItem> cloudNativeConfigResultItemList;

    public CloudNativeConfigResult getCloudNativeConfigResult() {
        return cloudNativeConfigResult;
    }

    public void setCloudNativeConfigResult(CloudNativeConfigResult cloudNativeConfigResult) {
        this.cloudNativeConfigResult = cloudNativeConfigResult;
    }

    public List<CloudNativeConfigResultItem> getCloudNativeConfigResultItemList() {
        return cloudNativeConfigResultItemList;
    }

    public void setCloudNativeConfigResultItemList(List<CloudNativeConfigResultItem> cloudNativeConfigResultItemList) {
        this.cloudNativeConfigResultItemList = cloudNativeConfigResultItemList;
    }
}
