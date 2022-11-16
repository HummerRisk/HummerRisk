package com.hummerrisk.oss.controller.request;

import com.hummerrisk.base.domain.OssWithBLOBs;

/**
 * harris
 */
public class OssBucketRequest extends OssWithBLOBs {

    private String bucketId;

    private String bucketName;

    private String extranetEndpoint;

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getExtranetEndpoint() {
        return extranetEndpoint;
    }

    public void setExtranetEndpoint(String extranetEndpoint) {
        this.extranetEndpoint = extranetEndpoint;
    }
}
