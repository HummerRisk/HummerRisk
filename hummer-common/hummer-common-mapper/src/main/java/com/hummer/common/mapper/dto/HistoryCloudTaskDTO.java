package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.HistoryCloudTask;

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
