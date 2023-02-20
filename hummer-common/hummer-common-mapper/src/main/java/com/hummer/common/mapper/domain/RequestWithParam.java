package com.hummer.common.mapper.domain;

import com.hummer.common.mapper.proxy.Request;

public class RequestWithParam extends Request {
    private String param;

    public RequestWithParam() {
        super("", "");
    }

    public RequestWithParam(String credential, String regionId, String param) {
        super(credential, regionId);
        this.param = param;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}