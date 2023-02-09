package com.hummer.common.core.proxy.aws;

import com.hummerrisk.base.domain.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyUtils {
    protected static Logger log = LoggerFactory.getLogger(ProxyUtils.class);

    public static AWSProxySetting getProxySetting(Proxy proxy) {
        try {
            if (proxy == null) return null;
            AWSProxySetting proxySetting = new AWSProxySetting();
            String host = proxy.getProxyIp();
            if (host != null && host.trim().length() > 0) {
                proxySetting.setHost(host.trim());
            } else {
                return null;
            }
            String portStr = proxy.getProxyPort();
            if (portStr != null && portStr.trim().length() > 0) {
                int port = Integer.parseInt(portStr);
                proxySetting.setPort(port);
            } else {
                log.info("AWS proxy port not set!");
                return null;
            }
            String userName = proxy.getProxyName();
            if (userName != null && userName.trim().length() > 0) {
                proxySetting.setUserName(userName.trim());
            }
            String password = proxy.getProxyPassword();
            if (password != null && password.trim().length() > 0) {
                proxySetting.setPassword(password.trim());
            }
            return proxySetting;
        } catch (NumberFormatException e) {
            log.info("AWS proxy port setting error!");
            return null;
        }
    }
}
