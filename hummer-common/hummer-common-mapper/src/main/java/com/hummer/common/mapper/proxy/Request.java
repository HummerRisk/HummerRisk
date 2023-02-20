package com.hummer.common.mapper.proxy;

public class Request {
    private String credential;
    private String regionId;


    public Request(String credential, String regionId) {
        this.credential = credential;
        this.regionId = regionId;
    }

    public Request() {
    }

    public String getCredential() {
        return this.credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getRegionId() {
        return this.regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

}

