package com.hummerrisk.proxy.k8s;

import com.google.gson.Gson;
import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.base.domain.CloudNativeSource;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.commons.utils.YamlUtil;
import com.hummerrisk.proxy.Request;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class K8sRequest extends Request {

    private K8sCredential k8sCredential;

    public K8sRequest() {
        super("", "");
    }

    public K8sRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public K8sCredential getK8sCredential() {
        if (k8sCredential == null) {
            k8sCredential = new Gson().fromJson(getCredential(), K8sCredential.class);
        }
        return k8sCredential;
    }

    public void setK8sCredential(K8sCredential k8sCredential) {
        this.k8sCredential = k8sCredential;
    }

    public String getToken() {
        k8sCredential = getK8sCredential();
        if (k8sCredential != null) {
            return k8sCredential.getToken();
        }
        return null;
    }

    public String getUrl() {
        k8sCredential = getK8sCredential();
        if (k8sCredential != null) {
            return k8sCredential.getUrl();
        }
        return null;
    }

    public ApiClient getK8sClient(Proxy proxy) throws IOException {
        if (getUrl() != null && getUrl().trim().length() > 0 && getToken() != null && getToken().trim().length() > 0) {
            /**创建默认 Api 客户端**/
            // 定义连接集群的 Token
            String token = k8sCredential.getToken();
            // 定义 Kubernetes 集群地址
            String url = k8sCredential.getUrl();
            // 配置客户端
            ApiClient apiClient = Config.fromToken(url, token, false);
            // 设置默认 Api 客户端到配置
            Configuration.setDefaultApiClient(apiClient);
            return apiClient;
        }
        return null;
    }

    public List<CloudNativeSource> getNameSpace(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NamespaceList result = apiInstance.listNamespace(pretty, true, null,
                    null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Namespace v1Namespace : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Namespace));
                cloudNativeSource.setSourceName(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Namespace.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getNode(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NodeList result = apiInstance.listNode(pretty, true, null,
                    null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Node v1Node : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Node.getMetadata().getNamespace()!=null?v1Node.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Node));
                cloudNativeSource.setSourceName(v1Node.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Node.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getPod(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PodList result = apiInstance.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Pod v1Pod : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Pod.getMetadata().getNamespace()!=null?v1Pod.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Pod));
                cloudNativeSource.setSourceName(v1Pod.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Pod.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getDeployment(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DeploymentList result = apiInstance.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Deployment v1Deployment : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Deployment.getMetadata().getNamespace()!=null?v1Deployment.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Deployment));
                cloudNativeSource.setSourceName(v1Deployment.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Deployment.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getDaemonSet(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DaemonSetList result = apiInstance.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1DaemonSet v1DaemonSet : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1DaemonSet.getMetadata().getNamespace()!=null?v1DaemonSet.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1DaemonSet));
                cloudNativeSource.setSourceName(v1DaemonSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.DaemonSet.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getService(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ServiceList result = apiInstance.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Service v1Service : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Service.getMetadata().getNamespace()!=null?v1Service.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Service));
                cloudNativeSource.setSourceName(v1Service.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Service.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getIngress(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1IngressList result = apiInstance.listIngressForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Ingress v1Ingress : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Ingress.getMetadata().getNamespace()!=null?v1Ingress.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Ingress));
                cloudNativeSource.setSourceName(v1Ingress.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Ingress.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getRole(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            RbacAuthorizationV1Api apiInstance = new RbacAuthorizationV1Api(apiClient);
            V1RoleList result = apiInstance.listRoleForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Role v1Role : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Role.getMetadata().getNamespace()!=null?v1Role.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Role));
                cloudNativeSource.setSourceName(v1Role.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Role.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getSecret(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1SecretList result = apiInstance.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Secret v1Secret : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Secret.getMetadata().getNamespace()!=null?v1Secret.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Secret));
                cloudNativeSource.setSourceName(v1Secret.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Secret.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getConfigMap(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ConfigMapList result = apiInstance.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1ConfigMap v1ConfigMap : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1ConfigMap.getMetadata().getNamespace()!=null?v1ConfigMap.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1ConfigMap));
                cloudNativeSource.setSourceName(v1ConfigMap.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.ConfigMap.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getStatefulSet(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1StatefulSetList result = apiInstance.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1StatefulSet v1StatefulSet : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1StatefulSet.getMetadata().getNamespace()!=null?v1StatefulSet.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1StatefulSet));
                cloudNativeSource.setSourceName(v1StatefulSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.StatefulSet.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getCronJob(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1beta1Api apiInstance = new BatchV1beta1Api(apiClient);
            V1beta1CronJobList result = apiInstance.listCronJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1beta1CronJob v1beta1CronJob : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1beta1CronJob.getMetadata().getNamespace()!=null?v1beta1CronJob.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1beta1CronJob));
                cloudNativeSource.setSourceName(v1beta1CronJob.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.CronJob.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getJob(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            V1JobList result = apiInstance.listJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Job v1Job : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Job.getMetadata().getNamespace()!=null?v1Job.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Job));
                cloudNativeSource.setSourceName(v1Job.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Job.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getPv(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeList result = apiInstance.listPersistentVolume(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1PersistentVolume v1PersistentVolume : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1PersistentVolume.getMetadata().getNamespace()!=null?v1PersistentVolume.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1PersistentVolume));
                cloudNativeSource.setSourceName(v1PersistentVolume.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getPvc(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeClaimList result = apiInstance.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1PersistentVolumeClaim v1PersistentVolumeClaim : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1PersistentVolumeClaim.getMetadata().getNamespace()!=null?v1PersistentVolumeClaim.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1PersistentVolumeClaim));
                cloudNativeSource.setSourceName(v1PersistentVolumeClaim.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getLease(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoordinationV1Api apiInstance = new CoordinationV1Api(apiClient);
            V1LeaseList result = apiInstance.listLeaseForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Lease v1Lease : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Lease.getMetadata().getNamespace()!=null?v1Lease.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1Lease));
                cloudNativeSource.setSourceName(v1Lease.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getEndpointSlice(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            DiscoveryV1Api apiInstance = new DiscoveryV1Api(apiClient);
            V1EndpointSliceList result = apiInstance.listEndpointSliceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1EndpointSlice v1EndpointSlice : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1EndpointSlice.getMetadata().getNamespace()!=null?v1EndpointSlice.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1EndpointSlice));
                cloudNativeSource.setSourceName(v1EndpointSlice.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getEvent(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            EventsV1Api apiInstance = new EventsV1Api(apiClient);
            EventsV1EventList result = apiInstance.listEventForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (EventsV1Event eventsV1Event : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(eventsV1Event.getMetadata().getNamespace()!=null?eventsV1Event.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(eventsV1Event));
                cloudNativeSource.setSourceName(eventsV1Event.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Event.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getNetworkPolicy(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1NetworkPolicyList result = apiInstance.listNetworkPolicyForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1NetworkPolicy v1NetworkPolicy : result.getItems()) {
                CloudNativeSource cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1NetworkPolicy.getMetadata().getNamespace()!=null?v1NetworkPolicy.getMetadata().getNamespace():"");
                cloudNativeSource.setSourceYaml(YamlUtil.toYaml(v1NetworkPolicy));
                cloudNativeSource.setSourceName(v1NetworkPolicy.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.NetworkPolicy.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public List<CloudNativeSource> getVersion(CloudNative cloudNative) throws IOException, ApiException {
        try {
            ApiClient apiClient = getK8sClient(null);
            VersionApi apiInstance = new VersionApi(apiClient);
            VersionInfo result = apiInstance.getCode();
            List<CloudNativeSource> list = new ArrayList<>();
            CloudNativeSource cloudNativeSource = base(cloudNative);
            cloudNativeSource.setSourceName(result.getGitVersion());
            cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Version.name());
            list.add(cloudNativeSource);
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (ApiException e) {
            throw new ApiException(e.getMessage());
        }
    }

    public CloudNativeSource base(CloudNative cloudNative) {
        CloudNativeSource cloudNativeSource = new CloudNativeSource();
        cloudNativeSource.setCloudNativeId(cloudNative.getId());
        cloudNativeSource.setCreator(SessionUtils.getUserId());
        cloudNativeSource.setCreateTime(System.currentTimeMillis());
        cloudNativeSource.setUpdateTime(System.currentTimeMillis());
        return cloudNativeSource;
    }
}
