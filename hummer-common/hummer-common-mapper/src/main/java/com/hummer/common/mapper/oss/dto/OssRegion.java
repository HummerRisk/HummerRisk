package com.hummer.common.mapper.oss.dto;

public class OssRegion {

    private String regionId;
    private String regionName;
    private String extranetEndpoint;
    private String intranetEndpoint;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getExtranetEndpoint() {
        return extranetEndpoint;
    }

    public void setExtranetEndpoint(String extranetEndpoint) {
        this.extranetEndpoint = extranetEndpoint;
    }

    public String getIntranetEndpoint() {
        return intranetEndpoint;
    }

    public void setIntranetEndpoint(String intranetEndpoint) {
        this.intranetEndpoint = intranetEndpoint;
    }
}
