package com.hummer.common.core.proxy.aliyun;


import com.hummer.common.core.constant.CloudAccountConstants;
import com.hummer.common.core.domain.Proxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProxyUtils {
    protected static Logger log = LoggerFactory.getLogger(ProxyUtils.class);

    public static AliyunProxySetting getProxySetting(Proxy proxy) {
        try {
            if (proxy == null) return null;
            AliyunProxySetting proxySetting = new AliyunProxySetting();
            boolean validSettings = false;
            if (StringUtils.equalsIgnoreCase(proxy.getProxyType(), CloudAccountConstants.ProxyType.Http.name())) {
                String httpHost = "http://" + proxy.getProxyIp() + ":" + proxy.getProxyPort();
                if (httpHost != null && httpHost.trim().length() > 0) {
                    proxySetting.setHttpAddr(httpHost.trim());
                    validSettings = true;
                }
            } else if (StringUtils.equalsIgnoreCase(proxy.getProxyType(), CloudAccountConstants.ProxyType.Https.name())) {
                String httpsHost = "http://" + proxy.getProxyName() + ":" + proxy.getProxyPassword() + "@" + proxy.getProxyIp() + ":" + proxy.getProxyPort();
                if (httpsHost != null && httpsHost.trim().length() > 0) {
                    proxySetting.setHttpsAddr(httpsHost.trim());
                    validSettings = true;
                }
            }
            if(!validSettings){
                return null;
            }
            String noProxy = null;
            if (noProxy != null && noProxy.trim().length() > 0) {
                proxySetting.setNoProxy(noProxy.trim());
            }
            return proxySetting;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
