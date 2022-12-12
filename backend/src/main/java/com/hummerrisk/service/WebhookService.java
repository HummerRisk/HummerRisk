package com.hummerrisk.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.hummerrisk.base.domain.SystemParameter;
import com.hummerrisk.base.mapper.SystemParameterMapper;
import com.hummerrisk.commons.constants.ParamConstants;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.EncryptUtils;
import com.hummerrisk.message.NotificationBasicResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WebhookService {

   
}
