package com.hummer.common.core.domain.request.rule;

import com.hummer.common.core.domain.RuleInspectionReport;

import java.util.Map;

public class RuleInspectionReportRequest extends RuleInspectionReport {

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }


}
