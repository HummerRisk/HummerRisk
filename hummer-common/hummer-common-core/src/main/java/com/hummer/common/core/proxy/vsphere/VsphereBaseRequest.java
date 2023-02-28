package com.hummer.common.core.proxy.vsphere;

import com.google.gson.Gson;
import com.hummer.common.core.exception.PluginException;
import com.hummer.common.core.proxy.Request;

import java.util.List;

public class VsphereBaseRequest extends Request {
    private VsphereCredential vsphereCredential;
    private String cluster;
    private List<String> hosts;

    private String zone; // host or cluster

    public VsphereBaseRequest() {
    }

    public VsphereBaseRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
    }

    public VsphereCredential getVsphereCredential() {
        if (vsphereCredential == null) {
            vsphereCredential = new Gson().fromJson(getCredential(), VsphereCredential.class);
        }
        return vsphereCredential;
    }

    public VsphereClient getVsphereClient() throws PluginException {
        VsphereCredential vCredential = getVsphereCredential();
        if (vCredential != null) {
            return new VsphereClient(vCredential.getvEndPoint(), vCredential.getvUserName(), vCredential.getvPassword(), getRegionId());
        }
        return null;
    }

    public String getZone() {
        if (zone == null || zone.trim().length() == 0) {
            if (cluster != null && cluster.trim().length() > 0) {
                this.zone = cluster;
            } else {
                this.zone = null;
            }
        }
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }
}
