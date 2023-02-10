package com.hummer.common.core.domain.request.k8s;

import com.hummerrisk.base.domain.CloudNativeResultItem;
import com.hummerrisk.base.domain.CloudNativeSource;
import com.hummerrisk.base.domain.CloudNativeSourceImage;

import java.util.List;

/**
 * harris
 */
public class NameSpaceImage extends CloudNativeSourceImage {

    private CloudNativeSource cloudNativeSource;

    private List<CloudNativeResultItem> cloudNativeResultItemList;

    public CloudNativeSource getCloudNativeSource() {
        return cloudNativeSource;
    }

    public void setCloudNativeSource(CloudNativeSource cloudNativeSource) {
        this.cloudNativeSource = cloudNativeSource;
    }

    public List<CloudNativeResultItem> getCloudNativeResultItemList() {
        return cloudNativeResultItemList;
    }

    public void setCloudNativeResultItemList(List<CloudNativeResultItem> cloudNativeResultItemList) {
        this.cloudNativeResultItemList = cloudNativeResultItemList;
    }
}
