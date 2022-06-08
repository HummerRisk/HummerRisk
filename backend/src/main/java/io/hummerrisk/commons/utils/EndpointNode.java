package io.hummerrisk.commons.utils;

import java.io.Serializable;

/**
 * @Author harris
 **/
public class EndpointNode implements Serializable {

    private String region;

    private String type;

    private String endpoint;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
