package com.hummer.system.dto;

import com.hummer.system.domain.ReportResult;

import java.util.List;
import java.util.Map;

public class ReportResultDTO extends ReportResult {

    private String userName;

    private List<String> checkedKeys;

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

    public List<String> getCheckedKeys() {
        return checkedKeys;
    }

    public void setCheckedKeys(List<String> checkedKeys) {
        this.checkedKeys = checkedKeys;
    }
}
