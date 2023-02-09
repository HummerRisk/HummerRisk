package com.hummer.common.core.proxy.rancher;

import com.google.gson.Gson;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.proxy.Request;
import io.kubernetes.client.openapi.ApiClient;

import java.io.IOException;

public class RancherRequest extends Request {

    private RancherCredential rancherCredential;

    public RancherRequest() {
        super("", "");
    }

    public RancherRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public RancherCredential getRancherCredential() {
        if (rancherCredential == null) {
            rancherCredential = new Gson().fromJson(getCredential(), RancherCredential.class);
        }
        return rancherCredential;
    }

    public void setRancherCredential(RancherCredential rancherCredential) {
        this.rancherCredential = rancherCredential;
    }

    public String getToken() {
        rancherCredential = getRancherCredential();
        if (rancherCredential != null) {
            return rancherCredential.getToken();
        }
        return null;
    }

    public String getUrl() {
        rancherCredential = getRancherCredential();
        if (rancherCredential != null) {
            return rancherCredential.getUrl();
        }
        return null;
    }

    public ApiClient getRancherClient(Proxy proxy) throws IOException {
        if (getUrl() != null && getUrl().trim().length() > 0 && getToken() != null && getToken().trim().length() > 0) {
            return null;
        }
        return null;
    }
}
