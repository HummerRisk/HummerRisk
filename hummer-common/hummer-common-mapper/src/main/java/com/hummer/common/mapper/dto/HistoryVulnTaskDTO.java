package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.HistoryVulnTask;

/**
 * @author harris
 */
public class HistoryVulnTaskDTO extends HistoryVulnTask {

    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
