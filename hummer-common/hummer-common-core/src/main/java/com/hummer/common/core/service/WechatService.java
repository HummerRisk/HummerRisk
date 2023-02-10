package com.hummer.common.core.service;

import com.alibaba.fastjson.JSONObject;
import com.hummer.common.core.constant.ParamConstants;
import com.hummer.common.core.domain.SystemParameter;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.mapper.SystemParameterMapper;
import com.hummer.common.core.message.NotificationBasicResponse;
import com.hummer.common.core.utils.EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WechatService {

    @Resource
    private SystemParameterMapper systemParameterMapper;
    @Resource
    private RestTemplate restTemplate;

    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}";
    private static final String MESSAGE_SEND_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    public String getAccessToken() {
        String cropId = getNotNullValue(ParamConstants.WECHAT.CROPID.getKey());
        String secret = EncryptUtils.aesDecrypt(getNotNullValue(ParamConstants.WECHAT.SECRET.getKey())).toString();
        Map<String, String> params = new HashMap<>();
        params.put("corpid", cropId);
        params.put("corpsecret", secret);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(ACCESS_TOKEN_URL, String.class, params);
        if (responseEntity.getStatusCodeValue() != 200) {
            HRException.throwException("request failed.");
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getIntValue("errcode") != 0) {
            HRException.throwException(jsonObject.getString("errmsg"));
        }
        return jsonObject.getString("access_token");
    }

    public NotificationBasicResponse sendTextMessageToUser(String content, List<String> to) {
        if (StringUtils.isEmpty(content) || to.isEmpty()) {
            HRException.throwException("required params can not be empty.");
        }
        String accessToken = getAccessToken();
        String url = MESSAGE_SEND_URL + "?access_token=" + accessToken;
        String agentId = getNotNullValue(ParamConstants.WECHAT.AGENTID.getKey());
        long l = 0;
        try {
            l = Long.parseLong(agentId);
        } catch (Exception e) {
            HRException.throwException("agentId is not valid.");
        }
        Map<String, Object> params = new HashMap<>();
        Map<String, String> contentStr = new HashMap<>();
        contentStr.put("content", content);
        params.put("msgtype", "text");
        params.put("agentid", l);
        params.put("text", contentStr);
        params.put("touser", buildReceiver(to));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<NotificationBasicResponse> responseResponseEntity = restTemplate.postForEntity(url, request, NotificationBasicResponse.class);
        return responseResponseEntity.getBody();
    }

    private String getValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter != null) {
            return systemParameter.getParamValue();
        }
        return null;
    }

    private String getNotNullValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            throw new RuntimeException(key + " not set");
        }
        String value = systemParameter.getParamValue();
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException(key + " not set");
        }
        return value;
    }

    private String buildReceiver(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String l : list) {
            stringBuilder.append(l).append("|");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
