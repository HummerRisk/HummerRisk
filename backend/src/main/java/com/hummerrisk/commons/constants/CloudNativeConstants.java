package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class CloudNativeConstants {

    public enum STATUS {
        DRAFT, UNCHECKED, APPROVED, FINISHED, TERMINATED, CANCELED, REJECTED, PROCESSING, ERROR, WARNING, RUNNING, PENDING, PAUSE, WAITING
    }

    /**
     * 风险等级:
     * 正常
     * 低风险
     * 中风险
     * 高风险
     * 重大风险
     */
    public enum Severity {
        Normal, LowRisk, MediumRisk, HighRisk, CriticalRisk
    }

    public enum K8S_TYPE {
        Namespace, Pod, Node, Deployment, DaemonSet, Service, Ingress, Role, Secret, ConfigMap,
        StatefulSet, CronJob, Job, PV, PVC, Lease, EndpointSlice, Event, NetworkPolicy, Version
    }

    public final static String URL1 = "/apis/aquasecurity.github.io/v1alpha1/configauditreports?limit=500";
    public final static String URL2 = "apis/aquasecurity.github.io/v1alpha1/configauditreports?limit=500";
    public final static String URL3 = "/apis/aquasecurity.github.io/v1alpha1/vulnerabilityreports?limit=500";
    public final static String URL4 = "apis/aquasecurity.github.io/v1alpha1/vulnerabilityreports?limit=500";
    public final static String URL5 = "/apis/aquasecurity.github.io/v1alpha1/namespaces/default/configauditreports?limit=500";
    public final static String URL6 = "apis/aquasecurity.github.io/v1alpha1/namespaces/default/configauditreports?limit=500";
    public final static String Accept = "application/json";
    public final static String TRIVY_OPERATOR = "trivy-operator";
    public final static String AQUASECURITY = "aquasecurity";

}
