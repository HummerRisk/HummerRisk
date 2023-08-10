package com.hummer.cloud.dto;

import com.hummer.common.core.domain.ReportResultDetail;

import java.util.Map;

public class ReportResultDetailDTO extends ReportResultDetail {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}