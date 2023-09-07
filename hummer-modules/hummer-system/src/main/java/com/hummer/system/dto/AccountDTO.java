package com.hummer.system.dto;

import com.hummer.common.core.domain.Account;
import com.hummer.common.core.domain.CloudTask;

import java.util.List;

public class AccountDTO {

    private Account account;

    private List<CloudTask> cloudTaskList;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<CloudTask> getCloudTaskList() {
        return cloudTaskList;
    }

    public void setCloudTaskList(List<CloudTask> cloudTaskList) {
        this.cloudTaskList = cloudTaskList;
    }
}
