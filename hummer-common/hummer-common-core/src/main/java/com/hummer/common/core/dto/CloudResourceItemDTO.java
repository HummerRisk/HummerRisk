package com.hummer.common.core.dto;


import com.hummer.common.core.domain.CloudResourceItem;

public class CloudResourceItemDTO extends CloudResourceItem {
    private Integer riskCount;

    public Integer getRiskCount() {
        return riskCount;
    }

    public void setRiskCount(Integer riskCount) {
        this.riskCount = riskCount;
    }
}
