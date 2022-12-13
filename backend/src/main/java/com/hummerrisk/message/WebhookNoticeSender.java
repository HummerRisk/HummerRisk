package com.hummerrisk.message;

import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.service.impl.SSLSocketFactoryImp;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.KeyStore;
import java.util.List;

@Component
public class WebhookNoticeSender extends AbstractNoticeSender {

    private void sendWebhook(MessageDetail messageDetail, NoticeModel noticeModel, String context) {
        HttpResponse response = null;
        try {
            List<String> webhookUrls = noticeModel.getWebhookUrls();
            for (String webhookUrl : webhookUrls) {
                HttpClient httpclient = HttpClients.createDefault();
                HttpPost httppost = new HttpPost(webhookUrl);
                httppost.addHeader("Content-Type", "application/json; charset=utf-8");
                //构建一个json格式字符串textMsg，其内容是接收方需要的参数和消息内容
                String textMsg1 = "{\"msgtype\":\"text\",\"text\":{\"content\":\"";
                String textMsg2 = "\"},\"at\":{\"atMobiles\":[\"xxx\"],\"isAtAll\":false}}";
                StringEntity se = new StringEntity(textMsg1 + context + textMsg2, "utf-8");
                httppost.setEntity(se);
                response = httpclient.execute(httppost);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String result = EntityUtils.toString(response.getEntity(), "utf-8");
                    LogUtil.debug(result);
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        } finally {
            try {
            } catch (Exception e) {
                LogUtil.error(e.getMessage());
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

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        sendWebhook(messageDetail, noticeModel, context);
    }
}
