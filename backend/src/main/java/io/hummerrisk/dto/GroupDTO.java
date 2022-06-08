package io.hummerrisk.dto;

import io.hummerrisk.base.domain.AccountWithBLOBs;

import java.util.List;


public class GroupDTO{

    private List<RuleGroupDTO> groups;

    private AccountWithBLOBs accountWithBLOBs;

    public List<RuleGroupDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<RuleGroupDTO> groups) {
        this.groups = groups;
    }

    public AccountWithBLOBs getAccountWithBLOBs() {
        return accountWithBLOBs;
    }

    public void setAccountWithBLOBs(AccountWithBLOBs accountWithBLOBs) {
        this.accountWithBLOBs = accountWithBLOBs;
    }
}
