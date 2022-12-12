package com.hummerrisk.message;

import com.hummerrisk.commons.utils.LogUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class WebhookNoticeSender extends AbstractNoticeSender {

    private void send(MessageDetail messageDetail, NoticeModel noticeModel, String context) {
        List<String> userIds = messageDetail.getUserIds();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            List<String> webhookUrls = noticeModel.getWebhookUrls();
            for (String webhookUrl : webhookUrls) {
                HttpPost httpPost = new HttpPost(webhookUrl);
                // 创建请求内容
                StringEntity entity = new StringEntity(context, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
                // 执行http请求
                response = httpClient.execute(httpPost);
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                LogUtil.error(e);
            }
        }
    }

    @Override
    public void send(MessageDetail messageDetail, NoticeModel noticeModel) {
        String context = super.getContext(messageDetail, noticeModel);
        send(messageDetail, noticeModel, context);
    }
}
