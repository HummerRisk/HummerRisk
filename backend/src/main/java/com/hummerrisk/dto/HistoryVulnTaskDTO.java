package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryVulnTask;

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
