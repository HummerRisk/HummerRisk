package com.hummer.common.core.domain.request.event;

import com.hummer.common.core.domain.CloudEventWithBLOBs;

/**
 * harris
 */
public class CloudEventWithBLOBsVo extends CloudEventWithBLOBs {

    private String accountName;

    private String accountIcon;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountIcon() {
        return accountIcon;
    }

    public void setAccountIcon(String accountIcon) {
        this.accountIcon = accountIcon;
    }
}
