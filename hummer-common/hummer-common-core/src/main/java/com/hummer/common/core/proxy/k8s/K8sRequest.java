package com.hummer.common.core.proxy.k8s;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hummer.common.core.constant.CloudNativeConstants;
import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.proxy.Request;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.common.core.utils.YamlUtil;
import io.gsonfire.builders.JsonObjectBuilder;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class K8sRequest extends Request {

    private K8sCredential k8sCredential;

    private  final String repoName = "hummer";
    private  final String name = "trivy-operator";
    private  final String namespace = "trivy-system";
    private  final String chart = "hummer/trivy-operator";
    private  final String version = "0.14.1";
    private  final String url = "https://registry.hummercloud.com/repository/charts";

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

}
