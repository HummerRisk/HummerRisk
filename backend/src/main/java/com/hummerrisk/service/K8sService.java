package com.hummerrisk.service;

import com.hummerrisk.base.domain.CloudNative;
import com.hummerrisk.base.mapper.CloudNativeMapper;
import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.utils.HttpClientUtil;
import com.hummerrisk.commons.utils.PlatformUtils;
import com.hummerrisk.proxy.k8s.K8sRequest;
import com.hummerrisk.service.impl.SSLSocketFactoryImp;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class K8sService {

    @Resource
    private CloudNativeMapper cloudNativeMapper;

    public void scan(String id) throws Exception {
        CloudNative cloudNative = cloudNativeMapper.selectByPrimaryKey(id);
        if (StringUtils.equalsIgnoreCase(PlatformUtils.k8s, cloudNative.getPluginId())) {
            K8sRequest k8sRequest = new K8sRequest();
            k8sRequest.setCredential(cloudNative.getCredential());
            String token = "Bearer " + k8sRequest.getToken();
            String url = k8sRequest.getUrl();
            if (url.endsWith("/")) {
                url = url + CloudNativeConstants.URL2;
            } else {
                url = url + CloudNativeConstants.URL1;
            }
            Map<String, String> param = new HashMap<>();
            param.put("Accept", "application/json;as=Table;v=v1;g=meta.k8s.io,application/json;as=Table;v=v1beta1;g=meta.k8s.io,application/json");
            param.put("User-Agent", "kubectl/v1.22.8 (linux/amd64) kubernetes/7061dbb");
            param.put("Authorization", token);
            String reponse = HttpClientUtil.HttpGet(url, param);

        } else if(StringUtils.equalsIgnoreCase(PlatformUtils.rancher, cloudNative.getPluginId())) {

        } else if(StringUtils.equalsIgnoreCase(PlatformUtils.openshift, cloudNative.getPluginId())) {

        } else if(StringUtils.equalsIgnoreCase(PlatformUtils.kubesphere, cloudNative.getPluginId())) {

        }
    }


}
