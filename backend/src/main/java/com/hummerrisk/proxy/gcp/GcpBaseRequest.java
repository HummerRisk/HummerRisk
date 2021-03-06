package com.hummerrisk.proxy.gcp;

import com.google.gson.Gson;
import com.hummerrisk.proxy.Request;

public class GcpBaseRequest extends Request {

    private GcpCredential gcpCredential;

    public GcpBaseRequest() {
    }

    public GcpBaseRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
    }

    public GcpCredential getGcpCredential() {
        if (gcpCredential == null) {
            gcpCredential = new Gson().fromJson(getCredential(), GcpCredential.class);
        }
        return gcpCredential;
    }

    public GcpClient getGcpClient() {
        GcpCredential gcpCredential = getGcpCredential();
        if (gcpCredential != null) {
            return new GcpClient();
        }
        return null;
    }

}
