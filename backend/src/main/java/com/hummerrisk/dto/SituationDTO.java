package com.hummerrisk.dto;

public class SituationDTO {

    private String namespaces;

    private String pods;

    private String nodes;

    private String deployments;

    private String daemonsets;

    private String services;

    private String ingress;

    private String roles;

    private String secrets;

    private String configmaps;

    private String statefulSets;

    private String cronJobs;

    private String jobs;

    private String versions;

    public String getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(String namespaces) {
        this.namespaces = namespaces;
    }

    public String getPods() {
        return pods;
    }

    public void setPods(String pods) {
        this.pods = pods;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getDeployments() {
        return deployments;
    }

    public void setDeployments(String deployments) {
        this.deployments = deployments;
    }

    public String getDaemonsets() {
        return daemonsets;
    }

    public void setDaemonsets(String daemonsets) {
        this.daemonsets = daemonsets;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getIngress() {
        return ingress;
    }

    public void setIngress(String ingress) {
        this.ingress = ingress;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSecrets() {
        return secrets;
    }

    public void setSecrets(String secrets) {
        this.secrets = secrets;
    }

    public String getConfigmaps() {
        return configmaps;
    }

    public void setConfigmaps(String configmaps) {
        this.configmaps = configmaps;
    }

    public String getStatefulSets() {
        return statefulSets;
    }

    public void setStatefulSets(String statefulSets) {
        this.statefulSets = statefulSets;
    }

    public String getCronJobs() {
        return cronJobs;
    }

    public void setCronJobs(String cronJobs) {
        this.cronJobs = cronJobs;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }
}
