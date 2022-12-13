package com.hummerrisk.http;

import com.hummerrisk.commons.constants.CloudNativeConstants;
import com.hummerrisk.commons.utils.HttpClientUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpTest {

    @org.junit.Test
    public void Test() throws Exception {
        try{
            String body = "test 测试";
            Map<String, String> param = new HashMap<>();
            param.put("Accept", CloudNativeConstants.Accept);
            HttpClientUtil.HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx", param, body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @org.junit.Test
    public void Test2() throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx");
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        //构建一个json格式字符串textMsg，其内容是接收方需要的参数和消息内容
        String content = "尊敬的用户, 您好, 您的源码安全风险检测结果如下:\n" +
                "\n" +
                "源码名称 : HummerRisk\n" +
                "\n" +
                "风险总数 : 32\n" +
                "\n" +
                "高危风险(Critical) : 8\n" +
                "\n" +
                "高风险(High) : 15\n" +
                "\n" +
                "中等风险(Medium) : 9\n" +
                "\n" +
                "低风险(Low) : 0\n" +
                "\n" +
                "无风险(Unknown) : 0\n" +
                "\n" +
                "注：更多详情请登录 HummerRisk 平台查看。";
        String textMsg1 = "{\"msgtype\":\"text\",\"text\":{\"content\":\"";
        String textMsg2 = "\"},\"at\":{\"atMobiles\":[\"xxx\"],\"isAtAll\":false}}";
        StringEntity se = new StringEntity(textMsg1 + content + textMsg2, "utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        }

    }

}
