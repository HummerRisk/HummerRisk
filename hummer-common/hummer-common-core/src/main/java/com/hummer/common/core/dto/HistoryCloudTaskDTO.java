package com.hummer.common.core.dto;


import com.hummer.common.core.domain.HistoryCloudTask;

/**
 * @author harris
 */
public class HistoryCloudTaskDTO extends HistoryCloudTask {

    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
