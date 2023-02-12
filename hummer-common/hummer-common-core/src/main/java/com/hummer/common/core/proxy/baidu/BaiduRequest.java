package com.hummer.common.core.proxy.baidu;

import com.baidubce.BceClientConfiguration;
import com.baidubce.BceClientException;
import com.baidubce.auth.BceV1Signer;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.auth.SignOptions;
import com.baidubce.common.BaseBceRequest;
import com.baidubce.common.HttpRequestHandler;
import com.baidubce.common.ServiceConfigFactory;
import com.baidubce.http.BceHttpClient;
import com.baidubce.http.HttpMethodName;
import com.baidubce.internal.InternalRequest;
import com.baidubce.internal.RestartableInputStream;
import com.baidubce.model.AbstractBceResponse;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.util.DateUtils;
import com.baidubce.util.HttpUtils;
import com.baidubce.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hummer.common.core.exception.PluginException;
import com.hummer.common.core.oss.dto.OssRegion;
import com.hummer.common.core.proxy.Request;
import com.hummer.common.core.utils.LogUtil;
import com.hummer.common.core.utils.ReadFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;


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

    public BceHttpClient getHttpClient() throws Exception {
        if (getAccessKey() != null && getSecretKey() != null) {

            BceClientConfiguration config = new BceClientConfiguration();

            // 设置HTTP最大连接数为10
            config.setMaxConnections(100);
            // 设置TCP连接超时为5000毫秒
            config.setConnectionTimeoutInMillis(50000);
            // 设置Socket传输数据超时的时间为2000毫秒
            config.setSocketTimeoutInMillis(20000);

            config.setEndpoint(getEndpoint(getRegionId()));
            config.setCredentials(new DefaultBceCredentials(baiduCredential.getAccessKeyId(), baiduCredential.getSecretAccessKey()));
            try {
                return new BceHttpClient(config,new BceV1Signer());
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

    public InternalRequest createRequest(HttpMethodName httpMethod, String path, Map<String, String> queries, Map<String, String> headers, BaseBceRequest bceRequest, String endpoint, String serviceId) throws Exception {
        URI uri = HttpUtils.appendUri(new URI(endpoint), new String[]{path});
        InternalRequest internalRequest = new InternalRequest(httpMethod, uri);
        SignOptions signOptions = new SignOptions();
        signOptions.setHeadersToSign(new HashSet(Arrays.asList(ServiceConfigFactory.getDefaultHeadersToSign(serviceId))));
        internalRequest.setSignOptions(signOptions);
        if (bceRequest == null) {
            bceRequest = new BaseBceRequest();
        } else {
            internalRequest.setCredentials(bceRequest.getRequestCredentials());
        }

        internalRequest.setHeaders(headers);
        internalRequest.setParameters(queries);
        HttpRequestHandler[] var9 = ServiceConfigFactory.getRequestHandlers(serviceId);
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            HttpRequestHandler httpRequestHandler = var9[var11];
            if (!httpRequestHandler.handle(internalRequest, bceRequest)) {
                break;
            }
        }

        if (internalRequest.getHttpMethod() == HttpMethodName.POST || internalRequest.getHttpMethod() == HttpMethodName.PUT) {
            String strJson = JsonUtils.toJsonString(bceRequest);

            byte[] requestJson;
            try {
                requestJson = strJson.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var13) {
                throw new BceClientException("Unsupported encode.", var13);
            }

            internalRequest.addHeader("Content-Length", String.valueOf(requestJson.length));
            internalRequest.addHeader("Content-Type", "application/json; charset=utf-8");
            internalRequest.setContent(RestartableInputStream.wrap(requestJson));
        }

        return internalRequest;
    }

    public QueryEventRequest createQueryEventRequest(){
        return new QueryEventRequest();
    }

    public  <T extends AbstractBceResponse> T execute(InternalRequest request, Class<T> responseClass, String serviceId) throws Exception {
        if (!request.getHeaders().containsKey("Content-Type")) {
            request.addHeader("Content-Type", "application/json; charset=utf-8");
        }

        if (!request.getHeaders().containsKey("Date")) {
            request.addHeader("Date", DateUtils.formatRfc822Date(new Date()));
        }
        return getHttpClient().execute(request,responseClass,ServiceConfigFactory.getResponseHandlers(serviceId));
    }

    @JsonIgnoreProperties(
            ignoreUnknown = true
    )
    public class QueryEventRequest extends BaseBceRequest {
        private String filters;
        private String startTime;
        private String endTime;
        private String pageNo;
        private String pageSize;

        public QueryEventRequest() {
        }

        public String getFilters() {
            return filters;
        }

        public void setFilters(String filters) {
            this.filters = filters;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getPageNo() {
            return pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        @Override
        public String toString() {
            return "QueryEventRequest{" +
                    "filters='" + filters + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", pageNo='" + pageNo + '\'' +
                    ", pageSize='" + pageSize + '\'' +
                    '}';
        }
    }

}
