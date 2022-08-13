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
        Namespace, Pod, Node, Deployment, DaemonSet, Service, Ingress, Role, Secret, ConfigMap, Version
    }

}
