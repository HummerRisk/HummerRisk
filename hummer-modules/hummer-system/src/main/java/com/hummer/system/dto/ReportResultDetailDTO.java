package com.hummer.system.dto;

import com.hummer.system.domain.ReportResultDetail;

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
