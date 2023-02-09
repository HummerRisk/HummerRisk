package com.hummer.common.core.proxy.kubesphere;

import com.google.gson.Gson;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.proxy.Request;
import io.kubernetes.client.openapi.ApiClient;

import java.io.IOException;

public class KubeSphereRequest extends Request {

    private KubeSphereCredential kubeSphereCredential;

    public KubeSphereRequest() {
        super("", "");
    }

    public KubeSphereRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public KubeSphereCredential getKubeSphereCredential() {
        if (kubeSphereCredential == null) {
            kubeSphereCredential = new Gson().fromJson(getCredential(), KubeSphereCredential.class);
        }
        return kubeSphereCredential;
    }

    public void setKubeSphereCredential(KubeSphereCredential kubeSphereCredential) {
        this.kubeSphereCredential = kubeSphereCredential;
    }

    public String getToken() {
        kubeSphereCredential = getKubeSphereCredential();
        if (kubeSphereCredential != null) {
            return kubeSphereCredential.getToken();
        }
        return null;
    }

    public String getUrl() {
        kubeSphereCredential = getKubeSphereCredential();
        if (kubeSphereCredential != null) {
            return kubeSphereCredential.getUrl();
        }
        return null;
    }

    public ApiClient getKubeSphereClient(Proxy proxy) throws IOException {
        if (getUrl() != null && getUrl().trim().length() > 0 && getToken() != null && getToken().trim().length() > 0) {
            return null;
        }
        return null;
    }
}
