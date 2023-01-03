package com.hummerrisk.proxy.k8s;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.base.domain.CloudNativeSourceImage;
import com.hummerrisk.base.domain.CloudNativeSourceWithBLOBs;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.UUIDUtil;
import com.hummerrisk.commons.utils.YamlUtil;
import com.hummerrisk.proxy.Request;
import io.gsonfire.builders.JsonObjectBuilder;
import io.kubernetes.client.ProtoClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.Meta;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

    public ApiClient getK8sClient(Proxy proxy) {
        if (getUrl() != null && getUrl().trim().length() > 0 && getToken() != null && getToken().trim().length() > 0) {
            // **创建默认 Api 客户端**/
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

    public void createOperatorChart() throws Exception {
        try {
            String name = "trivy-operator";
            String namespace = "trivy-system";
            String chart = "hummer/trivy-operator";
            String version = "0.9.1";

            ClassPathResource classPathResource = new ClassPathResource("file/values.yaml");
            File file = classPathResource.getFile();

            String values = (String) Yaml.load(file);

            JsonObject jsonObjectBuilder = new JsonObjectBuilder()
                    .set("apiVersion", "app.alauda.io/v1alpha1")
                    .set("kind", "HelmRequest")
                    .set("trivy.mode", "ClientServer")
                    .set("trivy.serverURL", k8sCredential.getIp() + ":" + k8sCredential.getPort())
                    .set("image.repository", "registry.cn-beijing.aliyuncs.com/hummerrisk/trivy-operator")
                    .set("trivy.ignoreUnfixed", true)
                    .set("trivy.repository", "registry.cn-beijing.aliyuncs.com/hummerrisk/trivy")
                    .set("metadata", new JsonObjectBuilder().set("name", name).build())
                    .set("spec", new JsonObjectBuilder()
                            .set("chart", chart)
                            .set("namespace", namespace)
                            .set("releaseName", name)
                            .set("values", values)
                            .set("version", version)
                    ).build();

            ApiClient apiClient = getK8sClient(null);
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
            Object result = customObjectsApi.createNamespacedCustomObject("aquasecurity.github.io", "v1alpha1", namespace, "helmrequests", jsonObjectBuilder, "true", null, null);

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    public void deleteOperatorChart() throws Exception {
        try {
            String namespace = "trivy-system";

            CustomObjectsApi customObjectsApi = new CustomObjectsApi(getK8sClient(null));
            customObjectsApi.deleteNamespacedCustomObject(
                    "aquasecurity.github.io",
                    "v1alpha1",
                    namespace,
                    "helmrequests",
                    "true",
                    0,
                    null,
                    null,
                    null,
                    new V1DeleteOptions().gracePeriodSeconds(0L).propagationPolicy("Foreground"));

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    public boolean listNamespacedCustomObject() throws ApiException, JsonProcessingException {
        try {
            String name = "trivy-operator";
            String namespace = "trivy-system";
            String chart = "hummer/trivy-operator";
            String version = "0.9.1";

            JsonObjectBuilder build = new JsonObjectBuilder().set("rootUser", "admin");
            JsonObjectBuilder type = new JsonObjectBuilder().set("type", "NodePort");

            JsonObjectBuilder values = new JsonObjectBuilder().set("service", type).set("auth", build);

            JsonObject jsonObjectBuilder = new JsonObjectBuilder()
                    .set("apiVersion", "app.alauda.io/v1alpha1")
                    .set("kind", "HelmRequest")
                    .set("trivy.mode", "ClientServer")
                    .set("trivy.serverURL", k8sCredential.getIp() + ":" + k8sCredential.getPort())
                    .set("image.repository", "registry.cn-beijing.aliyuncs.com/hummerrisk/trivy-operator")
                    .set("trivy.ignoreUnfixed", true)
                    .set("trivy.repository", "registry.cn-beijing.aliyuncs.com/hummerrisk/trivy")
                    .set("metadata", new JsonObjectBuilder().set("name", name).build())
                    .set("spec", new JsonObjectBuilder()
                            .set("chart", chart)
                            .set("namespace", namespace)
                            .set("releaseName", name)
                            .set("values", values)
                            .set("version", version)
                    ).build();

            ApiClient apiClient = getK8sClient(null);
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
            Object result = customObjectsApi.createNamespacedCustomObject("app.alauda.io", "v1alpha1", namespace, "helmrequests", jsonObjectBuilder, "true", null, null);

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }
        return true;
    }

    public void deleteKubenchJob() throws ApiException, IOException {
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            V1Status result = apiInstance.deleteNamespacedJob("kube-bench","default", null,null,null,null,null, null);
            LogUtil.debug(result.getStatus());
            LogUtil.debug("Success, Job 删除成功");
        } catch (ApiException e){
            LogUtil.error("Status code: {}"+ e.getCode());
            LogUtil.error("Reason: {}"+ e.getResponseBody());
            LogUtil.error("Response headers: {}"+ e.getResponseHeaders());
        } catch (Exception ex){
            LogUtil.error(ex.getMessage());
        }
    }

    public void deleteKubenchPod(String name) throws ApiException, IOException {
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1Pod result = apiInstance.deleteNamespacedPod(name,"default", null,null,null,null,null, null);
            LogUtil.debug(result.getStatus());
            LogUtil.debug("Success, Pod 删除成功");
        } catch (ApiException e){
            LogUtil.error("Status code: {}"+ e.getCode());
            LogUtil.error("Reason: {}"+ e.getResponseBody());
            LogUtil.error("Response headers: {}"+ e.getResponseHeaders());
        } catch (Exception ex){
            LogUtil.error(ex.getMessage());
        }
    }

    /**
     * 创建命名空间和通过Job资源对象文件来创建Job
     * @throws IOException
     * @throws ApiException
     */
    public void createKubenchJob() throws ApiException, IOException {
        ClassPathResource classPathResource = new ClassPathResource("file/kube-bench-job.yaml");
        File jobFile = classPathResource.getFile();
        V1Job body = (V1Job) Yaml.load(jobFile);
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            V1Job result = apiInstance.createNamespacedJob("default", body,null,null,null,null);
            LogUtil.debug("Success, Job 创建成功");
        } catch (ApiException e){
            if (e.getCode() == 409) {
                LogUtil.error("error Job 创建已重复！");
            } else if (e.getCode() == 200) {
                LogUtil.error("success Job 创建成功！");
            } else if (e.getCode() == 201) {
                LogUtil.error("error Job 创建已重复！");
            } else if (e.getCode() == 401) {
                LogUtil.error("error 无权限操作！");
            } else {
                LogUtil.error("error Job创建失败！");
            }
            LogUtil.error("Status code: {}"+ e.getCode());
            LogUtil.error("Reason: {}"+ e.getResponseBody());
            LogUtil.error("Response headers: {}"+ e.getResponseHeaders());
        } catch (Exception ex){
            LogUtil.error(ex.getMessage());
        }
    }

    public K8sSource getKubenchPod(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PodList result = apiInstance.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Pod v1Pod : result.getItems()) {
                if(!v1Pod.getMetadata().getName().contains("kube-bench-")) continue;
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Pod.getMetadata()).getNamespace() != null ? v1Pod.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Pod);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Pod.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1PodSpec spec = v1Pod.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = spec.getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1Pod.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Pod.name());
                cloudNativeSource.setSourceNode(v1Pod.getSpec().getNodeName() != null ? v1Pod.getSpec().getNodeName() : "");
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getNameSpace(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NamespaceList result = apiInstance.listNamespace(pretty, true, null,
                    null, null, null, null, null, null, null);
            for (V1Namespace v1Namespace : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Namespace.getMetadata()).getName());
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Namespace);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Namespace.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Namespace.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getNode(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NodeList result = apiInstance.listNode(pretty, true, null,
                    null, null, null, null, null, null, null);
            for (V1Node v1Node : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Node.getMetadata()).getNamespace() != null ? v1Node.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Node);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Node.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Node.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Node.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getPod(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PodList result = apiInstance.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Pod v1Pod : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Pod.getMetadata()).getNamespace() != null ? v1Pod.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Pod);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Pod.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1PodSpec spec = v1Pod.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = spec.getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1Pod.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Pod.name());
                cloudNativeSource.setSourceNode(v1Pod.getSpec().getNodeName() != null ? v1Pod.getSpec().getNodeName() : "");
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getDeployment(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DeploymentList result = apiInstance.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Deployment v1Deployment : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Deployment.getMetadata().getNamespace() != null ? v1Deployment.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Deployment);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Deployment.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1DeploymentSpec spec = v1Deployment.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = Objects.requireNonNull(spec.getTemplate().getSpec()).getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1Deployment.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Deployment.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getDaemonSet(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1DaemonSetList result = apiInstance.listDaemonSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1DaemonSet v1DaemonSet : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1DaemonSet.getMetadata().getNamespace() != null ? v1DaemonSet.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1DaemonSet);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1DaemonSet.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1DaemonSetSpec spec = v1DaemonSet.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = spec.getTemplate().getSpec().getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1DaemonSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.DaemonSet.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getService(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ServiceList result = apiInstance.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Service v1Service : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Service.getMetadata()).getNamespace() != null ? v1Service.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Service);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Service.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Service.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Service.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getIngress(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1IngressList result = apiInstance.listIngressForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Ingress v1Ingress : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Ingress.getMetadata()).getNamespace() != null ? v1Ingress.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Ingress);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Ingress.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Ingress.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Ingress.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getRole(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            RbacAuthorizationV1Api apiInstance = new RbacAuthorizationV1Api(apiClient);
            V1RoleList result = apiInstance.listRoleForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Role v1Role : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Role.getMetadata()).getNamespace() != null ? v1Role.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Role);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Role.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Role.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Role.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getSecret(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1SecretList result = apiInstance.listSecretForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Secret v1Secret : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1Secret.getMetadata().getNamespace() != null ? v1Secret.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Secret);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Secret.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Secret.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Secret.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getConfigMap(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1ConfigMapList result = apiInstance.listConfigMapForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1ConfigMap v1ConfigMap : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(v1ConfigMap.getMetadata().getNamespace() != null ? v1ConfigMap.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1ConfigMap);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1ConfigMap.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1ConfigMap.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.ConfigMap.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getStatefulSet(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            AppsV1Api apiInstance = new AppsV1Api(apiClient);
            V1StatefulSetList result = apiInstance.listStatefulSetForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1StatefulSet v1StatefulSet : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1StatefulSet.getMetadata()).getNamespace() != null ? v1StatefulSet.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1StatefulSet);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1StatefulSet.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1StatefulSetSpec spec = v1StatefulSet.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = spec.getTemplate().getSpec().getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1StatefulSet.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.StatefulSet.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getCronJob(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1beta1Api apiInstance = new BatchV1beta1Api(apiClient);
            V1beta1CronJobList result = apiInstance.listCronJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1beta1CronJob v1beta1CronJob : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1beta1CronJob.getMetadata()).getNamespace() != null ? v1beta1CronJob.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1beta1CronJob);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1beta1CronJob.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1beta1CronJobSpec spec = v1beta1CronJob.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = Objects.requireNonNull(Objects.requireNonNull(spec.getJobTemplate().getSpec()).getTemplate().getSpec()).getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1beta1CronJob.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.CronJob.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getJob(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            BatchV1Api apiInstance = new BatchV1Api(apiClient);
            V1JobList result = apiInstance.listJobForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Job v1Job : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Job.getMetadata()).getNamespace() != null ? v1Job.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Job);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Job.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                V1JobSpec spec = v1Job.getSpec();
                List<V1Container> v1Containers = null;
                if (spec != null) {
                    v1Containers = Objects.requireNonNull(spec.getTemplate().getSpec()).getContainers();
                    for (V1Container v1Container : v1Containers) {
                        CloudNativeSourceImage cloudNativeSourceImage = new CloudNativeSourceImage();
                        cloudNativeSourceImage.setImage(v1Container.getImage());
                        cloudNativeSourceImage.setCreateTime(System.currentTimeMillis());
                        cloudNativeSourceImage.setSourceId(cloudNativeSource.getId());
                        k8sSourceImage.add(cloudNativeSourceImage);
                    }
                }
                cloudNativeSource.setSourceName(v1Job.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Job.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getPv(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeList result = apiInstance.listPersistentVolume(null, null, null, null, null, null, null, null, null, null);
            for (V1PersistentVolume v1PersistentVolume : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1PersistentVolume.getMetadata()).getNamespace() != null ? v1PersistentVolume.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1PersistentVolume);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1PersistentVolume.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1PersistentVolume.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getPvc(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            V1PersistentVolumeClaimList result = apiInstance.listPersistentVolumeClaimForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1PersistentVolumeClaim v1PersistentVolumeClaim : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1PersistentVolumeClaim.getMetadata()).getNamespace() != null ? v1PersistentVolumeClaim.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1PersistentVolumeClaim);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1PersistentVolumeClaim.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1PersistentVolumeClaim.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.PVC.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getLease(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            CoordinationV1Api apiInstance = new CoordinationV1Api(apiClient);
            V1LeaseList result = apiInstance.listLeaseForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1Lease v1Lease : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1Lease.getMetadata()).getNamespace() != null ? v1Lease.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1Lease);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1Lease.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1Lease.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getEndpointSlice(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            DiscoveryV1Api apiInstance = new DiscoveryV1Api(apiClient);
            V1EndpointSliceList result = apiInstance.listEndpointSliceForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1EndpointSlice v1EndpointSlice : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1EndpointSlice.getMetadata()).getNamespace() != null ? v1EndpointSlice.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1EndpointSlice);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1EndpointSlice.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1EndpointSlice.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Lease.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getEvent(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            EventsV1Api apiInstance = new EventsV1Api(apiClient);
            EventsV1EventList result = apiInstance.listEventForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (EventsV1Event eventsV1Event : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(eventsV1Event.getMetadata()).getNamespace() != null ? eventsV1Event.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(eventsV1Event);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(eventsV1Event.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(eventsV1Event.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Event.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getNetworkPolicy(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            NetworkingV1Api apiInstance = new NetworkingV1Api(apiClient);
            V1NetworkPolicyList result = apiInstance.listNetworkPolicyForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
            for (V1NetworkPolicy v1NetworkPolicy : result.getItems()) {
                CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
                cloudNativeSource.setSourceNamespace(Objects.requireNonNull(v1NetworkPolicy.getMetadata()).getNamespace() != null ? v1NetworkPolicy.getMetadata().getNamespace() : "");
                JSONObject jsonObject;
                try {
                    jsonObject = (JSONObject) JSON.toJSON(v1NetworkPolicy);
                } catch (Exception e) {
                    jsonObject = (JSONObject) JSON.toJSON(v1NetworkPolicy.getMetadata());
                }
                cloudNativeSource.setSourceJson(jsonObject.toJSONString());
                cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
                cloudNativeSource.setSourceName(v1NetworkPolicy.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.NetworkPolicy.name());
                list.add(cloudNativeSource);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public K8sSource getVersion(CloudNative cloudNative) throws IOException, ApiException {
        K8sSource k8sSource = new K8sSource();
        List<CloudNativeSourceWithBLOBs> list = new ArrayList<>();
        List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();
        try {
            ApiClient apiClient = getK8sClient(null);
            VersionApi apiInstance = new VersionApi(apiClient);
            VersionInfo result = apiInstance.getCode();
            CloudNativeSourceWithBLOBs cloudNativeSource = base(cloudNative);
            cloudNativeSource.setSourceName(result.getGitVersion());
            JSONObject jsonObject = (JSONObject) JSON.toJSON(result);
            cloudNativeSource.setSourceJson(jsonObject.toJSONString());
            cloudNativeSource.setSourceYaml(YamlUtil.json2Yaml(jsonObject.toJSONString()));
            cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.Version.name());
            list.add(cloudNativeSource);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        k8sSource.setK8sSource(list);
        k8sSource.setK8sSourceImage(k8sSourceImage);
        return k8sSource;
    }

    public CloudNativeSourceWithBLOBs base(CloudNative cloudNative) {
        CloudNativeSourceWithBLOBs cloudNativeSource = new CloudNativeSourceWithBLOBs();
        cloudNativeSource.setId(UUIDUtil.newUUID());
        cloudNativeSource.setCloudNativeId(cloudNative.getId());
        cloudNativeSource.setCreateTime(System.currentTimeMillis());
        cloudNativeSource.setUpdateTime(System.currentTimeMillis());
        return cloudNativeSource;
    }
}
