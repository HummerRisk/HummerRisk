package com.hummer.common.core.proxy.openshift;

import com.google.gson.Gson;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.proxy.Request;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;

import java.io.IOException;

public class OpenShiftRequest extends Request {

    private OpenShiftCredential openShiftCredential;

    public OpenShiftRequest() {
        super("", "");
    }

    public OpenShiftRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public OpenShiftCredential getOpenShiftCredential() {
        if (openShiftCredential == null) {
            openShiftCredential = new Gson().fromJson(getCredential(), OpenShiftCredential.class);
        }
        return openShiftCredential;
    }

    public void setOpenShiftCredential(OpenShiftCredential openShiftCredential) {
        this.openShiftCredential = openShiftCredential;
    }

    public String getMaster() {
        openShiftCredential = getOpenShiftCredential();
        if (openShiftCredential != null) {
            return openShiftCredential.getMaster();
        }
        return null;
    }

    public String getUrl() {
        openShiftCredential = getOpenShiftCredential();
        if (openShiftCredential != null) {
            return openShiftCredential.getUrl();
        }
        return null;
    }

    public String getUserName() {
        openShiftCredential = getOpenShiftCredential();
        if (openShiftCredential != null) {
            return openShiftCredential.getUserName();
        }
        return null;
    }

    public String getPassword() {
        openShiftCredential = getOpenShiftCredential();
        if (openShiftCredential != null) {
            return openShiftCredential.getPassword();
        }
        return null;
    }

    public OpenShiftClient getOpenShiftClient(Proxy proxy) throws IOException {
        if (getUrl() != null && getUrl().trim().length() > 0 && getMaster() != null && getMaster().trim().length() > 0
        && getUserName() != null && getUserName().trim().length() > 0 && getPassword() != null && getPassword().trim().length() > 0) {
            OpenShiftConfig config = new OpenShiftConfigBuilder()
                    .withOpenShiftUrl(getUrl())
                    .withMasterUrl(getMaster())
                    .withUsername(getUserName())
                    .withPassword(getPassword())
                    .withTrustCerts(true).build();
            OpenShiftClient client = new DefaultOpenShiftClient(config);
            return client;
        }
        return null;
    }
}
