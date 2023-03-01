package com.hummer.cloud.oss.dto;

import com.hummer.common.core.domain.OssRegion;

import java.util.List;

public class BucketKeyValueItem {
    private List<OssRegion> locationList;
    private List<KeyValueItem> cannedAclList;
    private List<KeyValueItem> storageList;
    private boolean showLocation = false;
    private boolean showCannedAcl = false;
    private boolean showStorageClass = false;

    public List<OssRegion> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<OssRegion> locationList) {
        this.locationList = locationList;
    }

    public List<KeyValueItem> getCannedAclList() {
        return cannedAclList;
    }

    public void setCannedAclList(List<KeyValueItem> cannedAclList) {
        this.cannedAclList = cannedAclList;
    }

    public List<KeyValueItem> getStorageList() {
        return storageList;
    }

    public void setStorageList(List<KeyValueItem> storageList) {
        this.storageList = storageList;
    }

    public boolean isShowLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public boolean isShowCannedAcl() {
        return showCannedAcl;
    }

    public void setShowCannedAcl(boolean showCannedAcl) {
        this.showCannedAcl = showCannedAcl;
    }

    public boolean isShowStorageClass() {
        return showStorageClass;
    }

    public void setShowStorageClass(boolean showStorageClass) {
        this.showStorageClass = showStorageClass;
    }
}
