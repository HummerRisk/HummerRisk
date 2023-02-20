package com.hummer.common.mapper.domain.request.k8s;

/**
 * harris
 */
public class RiskRequest {

    private String risk;

    private String accountId;

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
