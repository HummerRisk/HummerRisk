package com.hummerrisk.dto;

import com.hummerrisk.base.domain.CloudResourceItem;

public class CloudResourceItemDTO extends CloudResourceItem {
    private Integer ruleCount;

    public Integer getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(Integer ruleCount) {
        this.ruleCount = ruleCount;
    }
}
