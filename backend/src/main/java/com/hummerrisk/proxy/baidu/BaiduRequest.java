package com.hummerrisk.proxy.baidu;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hummerrisk.commons.exception.PluginException;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ReadFileUtils;
import com.hummerrisk.oss.dto.OssRegion;
import com.hummerrisk.proxy.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class BaiduRequest extends Request {
    private BaiduCredential baiduCredential;

    public BaiduRequest() {
    }

    public BaiduRequest(Request req) {
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public BaiduCredential getBaiduCredential() {
        if (baiduCredential == null) {
            baiduCredential = new Gson().fromJson(getCredential(), BaiduCredential.class);
        }
        return baiduCredential;
    }

    public void setBaiduCredential(BaiduCredential baiduCredential) {
        this.baiduCredential = baiduCredential;
        setCredential(new Gson().toJson(baiduCredential));
    }

    public String getSecretKey() {
        baiduCredential = getBaiduCredential();
        if (baiduCredential != null) {
            return baiduCredential.getSecretAccessKey();
        }
        return null;
    }

    public String getAccessKey() {
        baiduCredential = getBaiduCredential();
        if (baiduCredential != null) {
            return baiduCredential.getAccessKeyId();
        }
        return null;
    }

    public <T> BosClient getClient() throws Exception {
        BosClient client = null;
        if (getAccessKey() != null && getSecretKey() != null) {

            BosClientConfiguration config = new BosClientConfiguration();

            // 设置HTTP最大连接数为10
            config.setMaxConnections(100);
            // 设置TCP连接超时为5000毫秒
            config.setConnectionTimeoutInMillis(50000);
            // 设置Socket传输数据超时的时间为2000毫秒
            config.setSocketTimeoutInMillis(20000);

            config.setEndpoint(getEndpoint(getRegionId()));
            config.setCredentials(new DefaultBceCredentials(baiduCredential.getAccessKeyId(), baiduCredential.getSecretAccessKey()));
            try {
                client = new BosClient(config);
            } catch (Exception e) {
                LogUtil.error(e.getMessage(), e);
                throw new PluginException(e);
            }
        }
        return client;
    }

    public <T> T getClient(Class<T> client) throws Exception {
        if (getAccessKey() != null && getSecretKey() != null) {

            BosClientConfiguration config = new BosClientConfiguration();

            // 设置HTTP最大连接数为10
            config.setMaxConnections(100);
            // 设置TCP连接超时为5000毫秒
            config.setConnectionTimeoutInMillis(50000);
            // 设置Socket传输数据超时的时间为2000毫秒
            config.setSocketTimeoutInMillis(20000);

            config.setEndpoint(getEndpoint(getRegionId()));
            config.setCredentials(new DefaultBceCredentials(baiduCredential.getAccessKeyId(), baiduCredential.getSecretAccessKey()));
            try {
                if(client == BosClient.class) {
                    BosClient bosClient = new BosClient(config);
                    return client.cast(bosClient);
                }else {
                    return null;
                }
            } catch (Exception e) {
                LogUtil.error(e.getMessage(), e);
                throw new PluginException(e);
            }
        }
        return null;
    }

    private String getEndpoint(String region) throws Exception {

        String endpoint = "";
        // 目前支持“华北-北京”、“华南-广州”和“华东-苏州”三个区域。北京区域：http://bj.bcebos.com，广州区域：http://gz.bcebos.com，苏州区域：http://su.bcebos.com
        List<OssRegion> regionList = this.getOssRegions();
        for(OssRegion ossRegion : regionList){
            if(StringUtils.equalsIgnoreCase(ossRegion.getRegionId(), getRegionId())){
                endpoint = ossRegion.getExtranetEndpoint();
                break;
            }
        }
        if(StringUtils.isBlank(endpoint)){
            // ENDPOINT参数只能用指定的包含区域的域名来进行定义，不指定时默认为北京区域
            endpoint = "http://bj.bcebos.com";
        }
        return endpoint;
    }

    public List<OssRegion> getOssRegions() throws Exception {
        String result = ReadFileUtils.readConfigFile("support/regions/", "hummer-baidu-plugin", ".json");
        return new Gson().fromJson(result, new TypeToken<ArrayList<OssRegion>>() {}.getType());
    }

}
