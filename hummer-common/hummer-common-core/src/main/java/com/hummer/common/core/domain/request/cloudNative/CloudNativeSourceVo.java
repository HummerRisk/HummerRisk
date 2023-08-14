package com.hummer.common.core.domain.request.cloudNative;

import java.io.Serializable;

/**
 * harris
 */
public class CloudNativeSourceVo implements Serializable {

    private String id;

    private String cloudNativeId;

    private String cloudNativeName;

    private String sourceName;

    private String sourceNamespace;

    private String sourceType;

    private Long createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCloudNativeId() {
        return cloudNativeId;
    }

    public void setCloudNativeId(String cloudNativeId) {
        this.cloudNativeId = cloudNativeId;
    }

    public String getCloudNativeName() {
        return cloudNativeName;
    }

    public void setCloudNativeName(String cloudNativeName) {
        this.cloudNativeName = cloudNativeName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceNamespace() {
        return sourceNamespace;
    }

    public void setSourceNamespace(String sourceNamespace) {
        this.sourceNamespace = sourceNamespace;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
