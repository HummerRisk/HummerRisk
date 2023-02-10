package com.hummer.common.core.utils;

import com.hummer.common.core.utils.imp.SSLSocketFactoryImp;
import com.hummer.common.core.constant.CloudNativeConstants;
import okhttp3.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    public static String doGet(String url, Map<String, String> param) throws Exception {

        CloseableHttpClient httpClient = null;
        String resultString = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createClient();

            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                throw e;
            }
            try {
                if (httpClient != null)
                    httpClient.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return resultString;
    }

    public static String doGet(String url) throws Exception {
        return doGet(url, null);
    }

    public static String doPost(String url, Map<String, String> param) throws Exception {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String resultString = null;
        try {
            httpClient = createClient();

            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                throw e;
            }
            try {
                if (httpClient != null)
                    httpClient.close();
            } catch (IOException e) {
                throw e;
            }
        }

        return resultString;
    }

    public static String doPost(String url) throws Exception {
        return doPost(url, null);
    }

    public static CloseableHttpClient createClient() throws Exception {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

//创建httpClient
        CloseableHttpClient client = HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        return client;
    }

    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    public static String HttpGet(String url, Map<String, String> param) throws Exception {
        OkHttpClient client = getClient();
        Request.Builder builder = new Request.Builder();
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addHeader(key, param.get(key));
            }
        }
        Request request = builder.url(url).method("GET", null).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    public static String HttpPost(String url, Map<String, String> param, String body) throws Exception {
        OkHttpClient client = getClient();
        Request.Builder builder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addHeader(key, param.get(key));
            }
        }
        Request request = builder.url(url).method("POST", requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        }

    }

    public static OkHttpClient getClient(Interceptor... interceptor) {
        OkHttpClient.Builder builder = null;
        try {
            builder = new OkHttpClient.Builder();
            //ssl verifier
            KeyStore trustStore;
            trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactoryImp ssl = new SSLSocketFactoryImp(KeyStore.getInstance(KeyStore.getDefaultType()));

            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            builder.sslSocketFactory(ssl.getSSLContext().getSocketFactory(), ssl.getTrustManager())
                    .hostnameVerifier(DO_NOT_VERIFY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //检测trivy-operator
    public static boolean operatorStatus(String url, Map<String, String> param) throws Exception {
        OkHttpClient client = getClient();
        Request.Builder builder = new Request.Builder();
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addHeader(key, param.get(key));
            }
        }
        Request request = builder.url(url).method("GET", null).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String res = response.body().string();
                return res.contains(CloudNativeConstants.TRIVY_OPERATOR) ||
                        res.contains(CloudNativeConstants.AQUASECURITY);
            } else {
                return false;
            }
        }
    }

    //检测kube-bench
    public static boolean kubenchStatus(String url, Map<String, String> param) throws Exception {
        OkHttpClient client = getClient();
        Request.Builder builder = new Request.Builder();
        if (param != null) {
            for (String key : param.keySet()) {
                builder.addHeader(key, param.get(key));
            }
        }
        Request request = builder.url(url).method("GET", null).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                response.body().string();
                return true;
            } else {
                return false;
            }
        }
    }

}
