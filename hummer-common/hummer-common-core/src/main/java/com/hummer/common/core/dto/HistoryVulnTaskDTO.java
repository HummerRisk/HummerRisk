package com.hummer.common.core.dto;


import com.hummer.common.core.domain.HistoryVulnTask;

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
