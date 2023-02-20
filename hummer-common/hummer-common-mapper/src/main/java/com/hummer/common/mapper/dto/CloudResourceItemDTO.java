package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.CloudResourceItem;

public class CloudResourceItemDTO extends CloudResourceItem {
    private Integer riskCount;

    public Integer getRiskCount() {
        return riskCount;
    }

    public void setRiskCount(Integer riskCount) {
        this.riskCount = riskCount;
    }
}
