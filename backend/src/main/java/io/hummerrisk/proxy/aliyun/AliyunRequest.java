package io.hummerrisk.proxy.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.http.HttpClientType;
import com.aliyuncs.http.clients.ApacheHttpClient;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import io.hummerrisk.base.domain.Proxy;
import io.hummerrisk.proxy.Request;

import java.io.IOException;

public class AliyunRequest extends Request {
    private AliyunCredential aliyunCredential;
    private HttpClientConfig httpClientConfig = HttpClientConfig.getDefault();

    public AliyunRequest() {
        super("", "");
    }

    public AliyunRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public AliyunCredential getAliyunCredential() {
        if (aliyunCredential == null) {
            aliyunCredential = new Gson().fromJson(getCredential(), AliyunCredential.class);
        }
        return aliyunCredential;
    }

    public void setAliyunCredential(AliyunCredential aliyunCredential) {
        this.aliyunCredential = aliyunCredential;
    }

    public String getSecretKey() {
        aliyunCredential = getAliyunCredential();
        if (aliyunCredential != null) {
            return aliyunCredential.getSecretKey();
        }
        return null;
    }

    public String getAccessKey() {
        aliyunCredential = getAliyunCredential();
        if (aliyunCredential != null) {
            return aliyunCredential.getAccessKey();
        }
        return null;
    }

    public IAcsClient getAliyunClient(Proxy proxy) throws IOException {
        if (getAccessKey() != null && getAccessKey().trim().length() > 0 && getSecretKey() != null && getSecretKey().trim().length() > 0) {
            String defaultRegionId = "cn-hangzhou";
            if (getRegionId() != null && getRegionId().trim().length() > 0) {
                defaultRegionId = getRegionId();
            }
            httpClientConfig.setReadTimeoutMillis(1800000L);//15m
            httpClientConfig.setWriteTimeoutMillis(300000L);//5m
            httpClientConfig.setConnectionTimeoutMillis(30000L);//30s
            httpClientConfig.setIgnoreSSLCerts(true);
            httpClientConfig.setCompatibleMode(false);
            httpClientConfig.setClientType(HttpClientType.ApacheHttpClient);
            AliyunProxySetting proxySetting = ProxyUtils.getProxySetting(proxy);
            httpClientConfig.setHttpProxy(proxySetting != null?proxySetting.getHttpAddr():null);
            httpClientConfig.setHttpsProxy(proxySetting != null?proxySetting.getHttpsAddr():null);
            httpClientConfig.setNoProxy(proxySetting != null?proxySetting.getNoProxy():null);
            DefaultProfile profile = DefaultProfile.getProfile(defaultRegionId, getAccessKey(), getSecretKey());
            profile.setHttpClientConfig(httpClientConfig);
            ApacheHttpClient.getInstance().close();
            DefaultAcsClient client = new DefaultAcsClient(profile);
            return client;

        }
        return null;
    }
}
