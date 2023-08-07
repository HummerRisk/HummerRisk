package com.hummer.common.core.dto;

import com.hummer.common.core.domain.request.task.AccountVo;

import java.util.List;

public class AccountTreeDTO {

    private List<AccountVo> cloudAccount;

    public List<AccountVo> getCloudAccount() {
        return cloudAccount;
    }

    public void setCloudAccount(List<AccountVo> cloudAccount) {
        this.cloudAccount = cloudAccount;
    }
}
