package com.hummer.common.mapper.proxy.huawei;

/**
 * @author harris
 */
public class BusiRequest extends IamRequest{

    private String busiType;

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }
}
