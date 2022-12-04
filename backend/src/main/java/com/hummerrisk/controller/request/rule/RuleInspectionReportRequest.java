package com.hummerrisk.controller.request.rule;

import com.hummerrisk.base.domain.RuleInspectionReport;

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
