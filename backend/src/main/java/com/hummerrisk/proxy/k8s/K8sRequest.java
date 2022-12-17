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

    public boolean createChart() throws ApiException, JsonProcessingException {
        try {
            String name = "mongodb-java-6666";
            String namespace = "test-hl2";
            String chart = "bitnami/mongodb";
            String version = "11.0.4";

            JsonObjectBuilder build = new JsonObjectBuilder().set("rootUser", "admin").set("rootPassword", "admin123123");
            JsonObjectBuilder type = new JsonObjectBuilder().set("type", "NodePort");

            JsonObjectBuilder values = new JsonObjectBuilder().set("service", type).set("auth", build);

            JsonObject jsonObjectBuilder = new JsonObjectBuilder()
                    .set("apiVersion", "app.alauda.io/v1alpha1")
                    .set("kind", "HelmRequest")
                    .set("metadata", new JsonObjectBuilder().set("name", name).build())
                    .set("spec", new JsonObjectBuilder()
                            .set("chart", chart)
                            .set("namespace", namespace)
                            .set("releaseName", name)
                            .set("values", values)
                            .set("version", version)
                    ).build();


            JsonNode jsonNode = new ObjectMapper().readTree(String.valueOf(jsonObjectBuilder));
            String s = new YAMLMapper().writeValueAsString(jsonNode);
            System.out.println(s);

            ApiClient apiClient = getK8sClient(new Proxy());
            CustomObjectsApi customObjectsApi = new CustomObjectsApi(apiClient);
            Object result = customObjectsApi.createNamespacedCustomObject("app.alauda.io", "v1alpha1", namespace, "helmrequests", jsonObjectBuilder, "true", null, null);

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean deleteChart() throws ApiException {
        try {
            String namespace = "test-hl2";
            String name = "mongodb-java-2";

            CustomObjectsApi customObjectsApi = new CustomObjectsApi(getK8sClient(new Proxy()));
            customObjectsApi.deleteNamespacedCustomObject(
                    "app.alauda.io",
                    "v1alpha1",
                    namespace,
                    "helmrequests",
                    name,
                    0,
                    null,
                    null,
                    null,
                    new V1DeleteOptions().gracePeriodSeconds(0L).propagationPolicy("Foreground"));

        } catch (Exception e) {
            LogUtil.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 大致流程
     * 1）创建一个命名空间
     * 2）根据资源对象文件创建deployment，创建多个副本，并指定自定义调度器
     * 3）通过API获取节点和pod
     * 4）选择phase=pending和schedulerName=my-scheduler的pod
     * 5）然后通过自定义的调度算法（这里只采用随机数的方法）计算出pod的合适目标节点。并使用Binding对象去绑定。
     */
    public class myScheduler {

        public File deploymentFile = new File("test-scheduler.yaml");

        /**
         * 删除对应的deployment
         * @param api
         * @param namespaceStr
         */
        private void deleteDeploymentByYaml(AppsV1Api api, String namespaceStr) throws ApiException {
            // 传入deployment的名字，命名空间，就可以删除deployment以及所有的pod了
            V1Status v1Status = api.deleteNamespacedDeployment("pause",namespaceStr,null,null,null,null,null,null);
            System.out.println(v1Status.getCode()+"删除完毕");
        }

        /**
         * 获得可用节点和判断pod的状态完成绑定
         * @param namespaceStr
         * @param api
         * @param client
         * @throws ApiException
         */
        private void getNodeAndPod(String namespaceStr, CoreV1Api api, ApiClient client) throws ApiException {
            Random random = new Random();
            // 调用方法，计算出所有可用的节点
            // 预选
            V1NodeList nodeList = ready_node(api);
            // 通过api获取指定命名空间(shiyan)中的pod
            V1PodList list = api.listNamespacedPod(namespaceStr,null,null,null,null,null,null,null, null, null, null);
            // 遍历list，循环判断pod的状态，并完成pod的调度
            for(V1Pod item:list.getItems()){
                // 优选
                int n = random.nextInt(nodeList.getItems().size());
                // 获取pod的状态
                String podStatus = item.getStatus().getPhase();
                String nodeName = item.getSpec().getNodeName();
                // 根据pod的状态和所属节点的名称进行绑定
                if (podStatus.equals("Pending") && nodeName == null){
                    // 执行调度方法 pod的名字；可用node;命名空间；客户端；api
                    schedluer(item.getMetadata().getName(),nodeList.getItems().get(n),namespaceStr,client,api);
                }
            }
        }

        /**
         * binding 绑定的过程
         * @param name
         * @param v1Node
         * @param namespaceStr
         * @param client
         * @param api
         * @throws ApiException
         */
        private void schedluer(String name, V1Node v1Node, String namespaceStr, ApiClient client, CoreV1Api api) throws ApiException {
            V1Binding body = new V1Binding();
            V1ObjectReference target = new V1ObjectReference();
            V1ObjectMeta meta = new V1ObjectMeta();
            target.setKind("Node");
            target.setApiVersion("v1");
            target.setName(v1Node.getMetadata().getName());  // 节点的名称
            meta.setName(name);
            body.setTarget(target);
            body.setMetadata(meta);
            api.createNamespacedBinding(namespaceStr,body,null,null,null, null);
        }


        /**
         * 获得可用的node，这个方法相当于预选阶段
         * @param api
         * @return
         * @throws ApiException
         */
        private V1NodeList ready_node(CoreV1Api api) throws ApiException {
            // 定义list装所有符合条件的节点
            V1NodeList nodeSelectList = new V1NodeList();
            // 通过api获取所有的节点
            V1NodeList nodeList = api.listNode(null, null,null,null,null,null,null,null,null,null);;
            // 遍历，找出能用的node，存进list中去
            for (V1Node node:nodeList.getItems()){
                List<V1NodeCondition> conditionsList = node.getStatus().getConditions();
                // 取出最后一个
                String status = conditionsList.get(conditionsList.size()-1).getStatus();
                String type = conditionsList.get(conditionsList.size()-1).getType();
                // 这里的预选策略相当于就是判断node节点的两个状态值
                if (status.equals("True")&&type.equals("Ready")){
                    nodeSelectList.addItemsItem(node);
                }
            }
            // 返回所有符合条件的节点
            return nodeSelectList;
        }

        /**
         * 创建命名空间和通过deployment资源对象文件来创建deployment
         * @param namespaceStr
         * @param apiInstance
         * @param protoClient
         * @throws IOException
         * @throws ApiException
         */
        private void createNameSpaceAndDepoyment(String namespaceStr, AppsV1Api apiInstance, ProtoClient protoClient) throws IOException, ApiException {
            // 1、创建一个命名空间
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance2 = new CoreV1Api(apiClient);
            V1Namespace v1Namespace = new V1Namespace();
            v1Namespace.setApiVersion("v1");
            v1Namespace.setKind("Namespace");
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(namespaceStr);
            v1Namespace.setMetadata(v1ObjectMeta);
            apiInstance2.createNamespace(v1Namespace,null, null,null, null);
            //   删除指定的命名空间
            //protoClient.delete(V1.Namespace.newBuilder(),"/api/v1/namespaces/"+namespaceStr);
            // 2、根据资源对象文件创建deployment，创建多个副本，并指定自定义调度器
            // File deploymentFile = new File("test-scheduler.yaml");
            V1Deployment body = (V1Deployment) Yaml.load(deploymentFile);
            try {
                V1Deployment result = apiInstance.createNamespacedDeployment(namespaceStr, body,null,null,null,null);
                System.out.println("success,工作负载创建成功");
            } catch (ApiException e){
                if (e.getCode() == 409) {
                    System.out.println("error 工作负载创建已重复！");
                } else if (e.getCode() == 200) {
                    System.out.println("success 工作负载创建成功！");
                } else if (e.getCode() == 201) {
                    System.out.println("error 工作负载创建已重复！");
                } else if (e.getCode() == 401) {
                    System.out.println("error 无权限操作！");
                } else {
                    System.out.println("error 工作负载创建失败！");
                }
                System.out.println("Exception when calling AppsV1Api#createNamespacedDeployment");
                System.out.println("Status code: {}"+ e.getCode());
                System.out.println("Reason: {}"+ e.getResponseBody());
                System.out.println("Response headers: {}"+ e.getResponseHeaders());
            }
        }
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
