package com.hummerrisk.proxy.k8s;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.base.domain.CloudNativeSourceWithBLOBs;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.utils.LogUtil;
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

    public List<CloudNativeSourceWithBLOBs> getNameSpace(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NamespaceList result = apiInstance.listNamespace(pretty, true, null,
                    null, null, null, null, null, null, null);
            for (V1Namespace v1Namespace : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Namespace.getMetadata().getName());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Namespace);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Namespace.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Namespace.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getNode(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NodeList result = apiInstance.listNode(pretty, true, null,
                    null, null, null, null, null, null, null);
            for (V1Node v1Node : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Node.getMetadata().getNamespace() != null ? v1Node.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Node);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Node.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Node.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Node.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getPod(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PodList result = apiInstance.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Pod v1Pod : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Pod.getMetadata().getNamespace() != null ? v1Pod.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Pod);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Pod.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Pod.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Pod.name());
                cloudNativeSource.setSourceNode(v1Pod.getSpec().getNodeName() != null ? v1Pod.getSpec().getNodeName() : "");
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getDeployment(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DeploymentList result = apiInstance.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Deployment v1Deployment : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Deployment.getMetadata().getNamespace() != null ? v1Deployment.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Deployment);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Deployment.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Deployment.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Deployment.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getDaemonSet(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DaemonSetList result = apiInstance.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1DaemonSet v1DaemonSet : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1DaemonSet.getMetadata().getNamespace() != null ? v1DaemonSet.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1DaemonSet);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1DaemonSet.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1DaemonSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.DaemonSet.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getService(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ServiceList result = apiInstance.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Service v1Service : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Service.getMetadata().getNamespace() != null ? v1Service.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Service);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Service.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Service.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Service.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getIngress(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1IngressList result = apiInstance.listIngressForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Ingress v1Ingress : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Ingress.getMetadata().getNamespace() != null ? v1Ingress.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Ingress);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Ingress.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Ingress.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Ingress.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getRole(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            RbacAuthorizationV1Api apiInstance = new RbacAuthorizationV1Api(apiClient);
            V1RoleList result = apiInstance.listRoleForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Role v1Role : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Role.getMetadata().getNamespace() != null ? v1Role.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Role);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Role.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Role.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Role.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getSecret(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1SecretList result = apiInstance.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Secret v1Secret : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Secret.getMetadata().getNamespace() != null ? v1Secret.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Secret);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Secret.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Secret.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Secret.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getConfigMap(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ConfigMapList result = apiInstance.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1ConfigMap v1ConfigMap : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1ConfigMap.getMetadata().getNamespace() != null ? v1ConfigMap.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1ConfigMap);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1ConfigMap.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1ConfigMap.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.ConfigMap.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getStatefulSet(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1StatefulSetList result = apiInstance.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1StatefulSet v1StatefulSet : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1StatefulSet.getMetadata().getNamespace() != null ? v1StatefulSet.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1StatefulSet);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1StatefulSet.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1StatefulSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.StatefulSet.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getCronJob(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1beta1Api apiInstance = new BatchV1beta1Api(apiClient);
            V1beta1CronJobList result = apiInstance.listCronJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1beta1CronJob v1beta1CronJob : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1beta1CronJob.getMetadata().getNamespace() != null ? v1beta1CronJob.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1beta1CronJob);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1beta1CronJob.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1beta1CronJob.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.CronJob.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getJob(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            V1JobList result = apiInstance.listJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Job v1Job : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Job.getMetadata().getNamespace() != null ? v1Job.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Job);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Job.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Job.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Job.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getPv(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeList result = apiInstance.listPersistentVolume(null, null, null, null, null, null, null, null, null, null);
            for (V1PersistentVolume v1PersistentVolume : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1PersistentVolume.getMetadata().getNamespace() != null ? v1PersistentVolume.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1PersistentVolume);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1PersistentVolume.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1PersistentVolume.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getPvc(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeClaimList result = apiInstance.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1PersistentVolumeClaim v1PersistentVolumeClaim : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1PersistentVolumeClaim.getMetadata().getNamespace() != null ? v1PersistentVolumeClaim.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1PersistentVolumeClaim);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1PersistentVolumeClaim.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1PersistentVolumeClaim.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getLease(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoordinationV1Api apiInstance = new CoordinationV1Api(apiClient);
            V1LeaseList result = apiInstance.listLeaseForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Lease v1Lease : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Lease.getMetadata().getNamespace() != null ? v1Lease.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1Lease);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1Lease.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Lease.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getEndpointSlice(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            DiscoveryV1Api apiInstance = new DiscoveryV1Api(apiClient);
            V1EndpointSliceList result = apiInstance.listEndpointSliceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1EndpointSlice v1EndpointSlice : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1EndpointSlice.getMetadata().getNamespace() != null ? v1EndpointSlice.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1EndpointSlice);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1EndpointSlice.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1EndpointSlice.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getEvent(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            EventsV1Api apiInstance = new EventsV1Api(apiClient);
            EventsV1EventList result = apiInstance.listEventForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (EventsV1Event eventsV1Event : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(eventsV1Event.getMetadata().getNamespace() != null ? eventsV1Event.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(eventsV1Event);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(eventsV1Event.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(eventsV1Event.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Event.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getNetworkPolicy(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1NetworkPolicyList result = apiInstance.listNetworkPolicyForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1NetworkPolicy v1NetworkPolicy : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1NetworkPolicy.getMetadata().getNamespace() != null ? v1NetworkPolicy.getMetadata().getNamespace() : "");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = (JSONObject) (JSONObject) JSONObject.toJSON(v1NetworkPolicy);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSONObject.toJSON(v1NetworkPolicy.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1NetworkPolicy.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.NetworkPolicy.name());
                list.add(cloudNativeSource);
            }
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public List<CloudNativeSourceWithBLOBs> getVersion(CloudNative cloudNative) throws IOException, ApiException {
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            VersionApi apiInstance = new VersionApi(apiClient);
            VersionInfo result = apiInstance.getCode();
            CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
            cloudNativeSource.setSourceName(result.getGitVersion());
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
            cloudNativeSource.setSourceJson(jsonObject.toJSONString());
            cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
            cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Version.name());
            list.add(cloudNativeSource);
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        } catch (ApiException e) {
            LogUtil.error(e.getMessage());
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return list;
    }

    public CloudNativeSourceWithBLOBs base(CloudNative cloudNative) {
        CloudNativeSourceWithBLOBs cloudNativeSource = new CloudNativeSourceWithBLOBs();
        cloudNativeSource.setCloudNativeId(cloudNative.getId());
        cloudNativeSource.setCreateTime(System.currentTimeMillis());
        cloudNativeSource.setUpdateTime(System.currentTimeMillis());
        return cloudNativeSource;
    }
}
