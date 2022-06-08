package io.hummerrisk.security;

import io.hummerrisk.base.domain.UserKey;
import io.hummerrisk.commons.utils.CodingUtil;
import io.hummerrisk.commons.utils.CommonBeanFactory;
import io.hummerrisk.service.UserKeyService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class ApiKeyHandler {

    public static final String API_ACCESS_KEY = "accessKey";

    public static final String API_SIGNATURE = "signature";

    public static String getUser(HttpServletRequest request) throws Exception {
        if (request == null) {
            return null;
        }
        return getUser(request.getHeader(API_ACCESS_KEY), request.getHeader(API_SIGNATURE));
    }

    public static Boolean isApiKeyCall(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return !StringUtils.isBlank(request.getHeader(API_ACCESS_KEY)) && !StringUtils.isBlank(request.getHeader(API_SIGNATURE));
    }

    public static String getUser(String accessKey, String signature) throws Exception {
        if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(signature)) {
            return null;
        }
        UserKey userKey = Objects.requireNonNull(CommonBeanFactory.getBean(UserKeyService.class)).getUserKey(accessKey);
        if (userKey == null) {
            throw new Exception("invalid accessKey");
        }
        String signatureDecrypt;
        try {
            signatureDecrypt = CodingUtil.aesDecrypt(signature, userKey.getSecretKey(), accessKey);
        } catch (Throwable t) {
            throw new Exception("invalid signature");
        }
        String[] signatureArray = StringUtils.split(StringUtils.trimToNull(signatureDecrypt), "|");
        if (signatureArray.length < 2) {
            throw new Exception("invalid signature");
        }
        if (!StringUtils.equals(accessKey, signatureArray[0])) {
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
