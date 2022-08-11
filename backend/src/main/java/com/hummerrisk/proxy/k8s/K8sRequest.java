package com.hummerrisk.proxy.k8s;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.base.domain.CloudNativeSource;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.exception.PluginException;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.proxy.Request;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
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
            K8sRequest k8sRequest = new K8sRequest();
            k8sRequest.setCredential(cloudNative.getCredential());
            this.k8sCredential = k8sRequest.getK8sCredential();
            ApiClient apiClient = getK8sClient(null);
            CoreV1Api apiInstance = new CoreV1Api(apiClient);
            String pretty = "true";
            V1NamespaceList result = apiInstance.listNamespace(pretty, true, null,
                    null, null, null, null, null, null, null);
            List<CloudNativeSource> list = new ArrayList<>();
            for (V1Namespace v1Namespace : result.getItems()) {
                CloudNativeSource cloudNativeSource = new CloudNativeSource();
                cloudNativeSource.setCloudNativeId(cloudNative.getId());
                cloudNativeSource.setCreator(SessionUtils.getUserId());
                cloudNativeSource.setCreateTime(System.currentTimeMillis());
                cloudNativeSource.setUpdateTime(System.currentTimeMillis());
                cloudNativeSource.setSourceNamespace(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceName(v1Namespace.getMetadata().getName());
                cloudNativeSource.setSourceType(CloudNativeConstants.K8S_TYPE.namespace.name());
                list.add(cloudNativeSource);
            }
            return list;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
