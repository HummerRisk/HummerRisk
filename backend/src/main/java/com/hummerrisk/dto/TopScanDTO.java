package com.hummerrisk.dto;

public class TopScanDTO {

    private String status;

    private String cloud;

    private String vuln;

    private String server;

    private String image;

    private String code;

    private String k8s;

    private String config;

    private String fs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getVuln() {
        return vuln;
    }

    public void setVuln(String vuln) {
        this.vuln = vuln;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getK8s() {
        return k8s;
    }

    public void setK8s(String k8s) {
        this.k8s = k8s;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getFs() {
        return fs;
    }

    public void setFs(String fs) {
        this.fs = fs;
    }
}
