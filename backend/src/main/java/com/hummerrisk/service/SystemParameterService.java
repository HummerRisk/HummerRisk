package com.hummerrisk.service;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.hummerrisk.base.domain.SystemParameter;
import com.hummerrisk.base.domain.SystemParameterExample;
import com.hummerrisk.base.mapper.SystemParameterMapper;
import com.hummerrisk.commons.constants.ParamConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.EncryptUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.message.NotificationBasicResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author harris
 */
@Service
public class SystemParameterService {

    @Resource
    private SystemParameterMapper systemParameterMapper;
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private Environment env;

    @Resource
    private SysListener sysListener;

    public String getSystemLanguage() {
        String result = StringUtils.EMPTY;
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyEqualTo(ParamConstants.I18n.LANGUAGE.getValue());
        List<SystemParameter> list = systemParameterMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            String value = list.get(0).getParamValue();
            if (StringUtils.isNotBlank(value)) {
                result = value;
            }
        }
        return result;
    }

    public void editMail(List<SystemParameter> parameters) {
        parameters.forEach(parameter -> {
            SystemParameterExample example = new SystemParameterExample();
            if (parameter.getParamKey().equals(ParamConstants.MAIL.PASSWORD.getKey()) &&
                    !StringUtils.isBlank(parameter.getParamValue())) {
                String string = null;
                try {
                    string = EncryptUtils.aesEncrypt(parameter.getParamValue()).toString();
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                }
                parameter.setParamValue(string);
            }
            example.createCriteria().andParamKeyEqualTo(parameter.getParamKey());
            if (systemParameterMapper.countByExample(example) > 0) {
                systemParameterMapper.updateByPrimaryKey(parameter);
            } else {
                systemParameterMapper.insert(parameter);
            }
            example.clear();
        });
    }

    public void editWechat(List<SystemParameter> parameters) {
        parameters.forEach(parameter -> {
            SystemParameterExample example = new SystemParameterExample();
            if (parameter.getParamKey().equals(ParamConstants.WECHAT.SECRET.getKey()) &&
                    !StringUtils.isBlank(parameter.getParamValue())) {
                String string = null;
                try {
                    string = EncryptUtils.aesEncrypt(parameter.getParamValue()).toString();
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                }
                parameter.setParamValue(string);
            }
            example.createCriteria().andParamKeyEqualTo(parameter.getParamKey());
            if (systemParameterMapper.countByExample(example) > 0) {
                systemParameterMapper.updateByPrimaryKey(parameter);
            } else {
                systemParameterMapper.insert(parameter);
            }
            example.clear();
        });
    }

    public void editDingding(List<SystemParameter> parameters) {
        parameters.forEach(parameter -> {
            SystemParameterExample example = new SystemParameterExample();
            if (parameter.getParamKey().equals(ParamConstants.DINGDING.APPSECRET.getKey()) &&
                    !StringUtils.isBlank(parameter.getParamValue())) {
                String string = null;
                try {
                    string = EncryptUtils.aesEncrypt(parameter.getParamValue()).toString();
                } catch (Exception e) {
                    LogUtil.error(e.getMessage());
                }
                parameter.setParamValue(string);
            }
            example.createCriteria().andParamKeyEqualTo(parameter.getParamKey());
            if (systemParameterMapper.countByExample(example) > 0) {
                systemParameterMapper.updateByPrimaryKey(parameter);
            } else {
                systemParameterMapper.insert(parameter);
            }
            example.clear();
        });
    }

    public void editMessage(List<SystemParameter> parameters) {
        parameters.forEach(parameter -> {
            SystemParameterExample example = new SystemParameterExample();
            example.createCriteria().andParamKeyEqualTo(parameter.getParamKey());
            if (systemParameterMapper.countByExample(example) > 0) {
                systemParameterMapper.updateByPrimaryKey(parameter);
            } else {
                systemParameterMapper.insert(parameter);
            }
            example.clear();
        });
    }

    public List<SystemParameter> getParamList(String type) {
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyLike(type + "%");
        return systemParameterMapper.selectByExample(example);
    }

    public void testEmailConnection(Map<String, String> hashMap) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setHost(hashMap.get(ParamConstants.MAIL.SERVER.getKey()));
        javaMailSender.setPort(Integer.parseInt(hashMap.get(ParamConstants.MAIL.PORT.getKey())));
        javaMailSender.setUsername(hashMap.get(ParamConstants.MAIL.ACCOUNT.getKey()));
        javaMailSender.setPassword(hashMap.get(ParamConstants.MAIL.PASSWORD.getKey()));
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        if (BooleanUtils.toBoolean(hashMap.get(ParamConstants.MAIL.SSL.getKey())))
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        if (BooleanUtils.toBoolean(hashMap.get(ParamConstants.MAIL.TLS.getKey())))
            props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.connectiontimeout", "5000");
        javaMailSender.setJavaMailProperties(props);
        try {
            javaMailSender.testConnection();
        } catch (MessagingException e) {
            HRException.throwException(Translator.get("connection_failed"));
        }
    }

    public void testWechatConnection(Map<String, String> hashMap) {
        String cropId = hashMap.get(ParamConstants.WECHAT.CROPID.getKey());
        String secret = hashMap.get(ParamConstants.WECHAT.SECRET.getKey());
        Map<String, String> params = new HashMap<>();
        params.put("corpid", cropId);
        params.put("corpsecret", secret);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}", String.class, params);
        if (responseEntity.getStatusCodeValue() != 200) {
            HRException.throwException("request failed.");
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getIntValue("errcode") != 0) {
            HRException.throwException(jsonObject.getString("errmsg"));
        }
        String token = jsonObject.getString("access_token");
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        String agentId = hashMap.get(ParamConstants.WECHAT.AGENTID.getKey());
        long l = 0;
        try {
            l = Long.parseLong(agentId);
        } catch (Exception e) {
            HRException.throwException("agentId is not valid.");
        }
        Map<String, Object> paramsStr = new HashMap<>();
        Map<String, String> contentStr = new HashMap<>();
        contentStr.put("content", "本消息由 HummerRisk 发送，仅为测试。");
        paramsStr.put("msgtype", "text");
        paramsStr.put("agentid", l);
        paramsStr.put("text", contentStr);
        paramsStr.put("touser", hashMap.get(ParamConstants.WECHAT.TESTUSER.getKey()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paramsStr, headers);

        ResponseEntity<NotificationBasicResponse> responseResponseEntity = restTemplate.postForEntity(url, request, NotificationBasicResponse.class);
        if (Objects.requireNonNull(responseResponseEntity.getBody()).getErrcode() != 0) {
            HRException.throwException(responseResponseEntity.getBody().getErrmsg());
        }
    }

    public void testDingtalkConnection(Map<String, String> hashMap) throws Exception {
        String ak = hashMap.get(ParamConstants.DINGDING.APPKEY.getKey());
        String sk = hashMap.get(ParamConstants.DINGDING.APPSECRET.getKey());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(ak);
        request.setAppsecret(sk);
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        if (response.getErrcode() != 0) {
            HRException.throwException(response.getErrmsg());
        }
        String token = response.getAccessToken();

        DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request1 = new OapiUserGetByMobileRequest();
        request1.setMobile(hashMap.get(ParamConstants.DINGDING.TESTUSER.getKey()));
        OapiUserGetByMobileResponse execute = client1.execute(request1, token);
        if (execute.getErrcode() != 0) {
            HRException.throwException(execute.getErrmsg());
        }
        String userId = execute.getUserid();
        String agentId = hashMap.get(ParamConstants.DINGDING.AGENTID.getKey());

        DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request2 = new OapiMessageCorpconversationAsyncsendV2Request();
        request2.setUseridList(userId);
        request2.setAgentId(Long.valueOf(agentId));
        request2.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent("本消息由 HummerRisk 发送，仅为测试。");
        request2.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response execute1 = client2.execute(request2, token);
        if (execute1.getErrcode() != 0) {
            HRException.throwException(execute1.getErrmsg());
        }
    }

    public String getVersion() {
        return env.getProperty("HR_VERSION");
    }

    public List<SystemParameter> info(String type) {
        List<SystemParameter> paramList = this.getParamList(type);
        if (!StringUtils.equalsIgnoreCase(type, ParamConstants.Classify.MAIL.getValue())) return paramList;
        if (CollectionUtils.isEmpty(paramList)) {
            paramList = new ArrayList<>();
            ParamConstants.MAIL[] values = ParamConstants.MAIL.values();
            for (ParamConstants.MAIL value : values) {
                SystemParameter systemParameter = new SystemParameter();
                if (value.equals(ParamConstants.MAIL.PASSWORD)) {
                    systemParameter.setType(ParamConstants.Type.PASSWORD.getValue());
                } else {
                    systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                }
                systemParameter.setParamKey(value.getKey());
                systemParameter.setSort(value.getValue());
                paramList.add(systemParameter);
            }
        } else {
            paramList.stream().filter(param -> param.getParamKey().equals(ParamConstants.MAIL.PASSWORD.getKey())).forEach(param -> {
                if (!StringUtils.isBlank(param.getParamValue())) {
                    String string = EncryptUtils.aesDecrypt(param.getParamValue()).toString();
                    param.setParamValue(string);
                }

            });
        }
        paramList.sort(Comparator.comparingInt(SystemParameter::getSort));
        return paramList;
    }

    public List<SystemParameter> wechatInfo(String type) {
        List<SystemParameter> paramList = this.getParamList(type);
        if (!StringUtils.equalsIgnoreCase(type, ParamConstants.Classify.WECHAT.getValue())) return paramList;
        if (CollectionUtils.isEmpty(paramList)) {
            paramList = new ArrayList<>();
            ParamConstants.WECHAT[] values = ParamConstants.WECHAT.values();
            for (ParamConstants.WECHAT value : values) {
                SystemParameter systemParameter = new SystemParameter();
                if (value.equals(ParamConstants.WECHAT.SECRET)) {
                    systemParameter.setType(ParamConstants.Type.PASSWORD.getValue());
                } else {
                    systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                }
                systemParameter.setParamKey(value.getKey());
                systemParameter.setSort(value.getValue());
                paramList.add(systemParameter);
            }
        } else {
            paramList.stream().filter(param -> param.getParamKey().equals(ParamConstants.WECHAT.SECRET.getKey())).forEach(param -> {
                if (!StringUtils.isBlank(param.getParamValue())) {
                    String string = EncryptUtils.aesDecrypt(param.getParamValue()).toString();
                    param.setParamValue(string);
                }

            });
        }
        paramList.sort(Comparator.comparingInt(SystemParameter::getSort));
        return paramList;
    }

    public List<SystemParameter> dingdingInfo(String type) {
        List<SystemParameter> paramList = this.getParamList(type);
        if (!StringUtils.equalsIgnoreCase(type, ParamConstants.Classify.DINGDING.getValue())) return paramList;
        if (CollectionUtils.isEmpty(paramList)) {
            paramList = new ArrayList<>();
            ParamConstants.DINGDING[] values = ParamConstants.DINGDING.values();
            for (ParamConstants.DINGDING value : values) {
                SystemParameter systemParameter = new SystemParameter();
                if (value.equals(ParamConstants.DINGDING.APPSECRET)) {
                    systemParameter.setType(ParamConstants.Type.PASSWORD.getValue());
                } else {
                    systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                }
                systemParameter.setParamKey(value.getKey());
                systemParameter.setSort(value.getValue());
                paramList.add(systemParameter);
            }
        } else {
            paramList.stream().filter(param -> param.getParamKey().equals(ParamConstants.DINGDING.APPSECRET.getKey())).forEach(param -> {
                if (!StringUtils.isBlank(param.getParamValue())) {
                    String string = EncryptUtils.aesDecrypt(param.getParamValue()).toString();
                    param.setParamValue(string);
                }

            });
        }
        paramList.sort(Comparator.comparingInt(SystemParameter::getSort));
        return paramList;
    }

    public String getValue(String key) {
        SystemParameter param = systemParameterMapper.selectByPrimaryKey(key);
        if (param == null) {
            return null;
        }
        return param.getParamValue();
    }

    public void updateSystem() throws Exception {
        ConcurrentHashMap<String, String> maps = sysListener.getMaps();
        maps.forEach((key,value)-> {
            if (!value.isEmpty() && !value.contains("null") && !value.contains("NaN")) {
                SystemParameterExample example = new SystemParameterExample();
                example.createCriteria().andParamKeyEqualTo(key);
                SystemParameter parameter = new SystemParameter();
                parameter.setParamKey(key);
                parameter.setParamValue(value);
                parameter.setType(ParamConstants.Classify.SYSTEM.getValue());
                parameter.setSort(1);
                if (systemParameterMapper.countByExample(example) > 0) {
                    systemParameterMapper.updateByPrimaryKey(parameter);
                } else {
                    systemParameterMapper.insertSelective(parameter);
                }
                example.clear();
            }
        });
    }
}
