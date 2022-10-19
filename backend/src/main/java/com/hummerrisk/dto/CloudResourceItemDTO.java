package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudResourceItem;

public class CloudResourceItemDTO extends CloudResourceItem {
    private Integer riskCount;

    public Integer getRiskCount() {
        return riskCount;
    }

    public void setRiskCount(Integer riskCount) {
        this.riskCount = riskCount;
    }
}
