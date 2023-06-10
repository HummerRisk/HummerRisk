package com.hummer.gateway.service;

import com.hummer.common.core.constant.ApiKeyConstants;
import com.hummer.common.core.domain.UserKey;
import com.hummer.common.core.domain.UserKeyExample;
import com.hummer.common.core.utils.CodingUtil;
import com.hummer.gateway.mapper.UserKeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ApiKeyService {

    @Autowired
    private UserKeyMapper userKeyMapper;

    public  String getUser(String accessKey, String signature) throws Exception {
        if (org.apache.commons.lang3.StringUtils.isBlank(accessKey) || org.apache.commons.lang3.StringUtils.isBlank(signature)) {
            return null;
        }
        UserKey userKey = userKeyMapper.getUserKey(accessKey);

        if (userKey == null) {
            throw new Exception("invalid accessKey");
        }
        String signatureDecrypt;
        try {
            signatureDecrypt = CodingUtil.aesDecrypt(signature, userKey.getSecretKey(), accessKey);
        } catch (Throwable t) {
            throw new Exception("invalid signature");
        }
        String[] signatureArray = org.apache.commons.lang3.StringUtils.split(org.apache.commons.lang3.StringUtils.trimToNull(signatureDecrypt), "|");
        if (signatureArray.length < 2) {
            throw new Exception("invalid signature");
        }
        if (!org.apache.commons.lang3.StringUtils.equals(accessKey, signatureArray[0])) {
            throw new Exception("invalid signature");
        }
        long signatureTime;
        try {
            signatureTime = Long.parseLong(signatureArray[signatureArray.length - 1]);
        } catch (Exception e) {
            throw new Exception(e);
        }
        if (Math.abs(System.currentTimeMillis() - signatureTime) > 1800000) {
            //签名30分钟超时
            throw new Exception("expired signature");
        }
        return userKey.getUserId();
    }

}

