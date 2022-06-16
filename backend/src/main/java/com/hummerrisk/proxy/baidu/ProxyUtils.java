package com.hummerrisk.proxy.baidu;


import com.baidubce.services.bcc.BccClientConfiguration;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CloudAccountConstants;
import org.apache.commons.lang3.StringUtils;


public class ProxyUtils {

    public static BccClientConfiguration getProxySetting(Proxy proxy) {
        try {
            if (proxy == null) return null;
            // 创建BccClientConfiguration实例
            BccClientConfiguration config = new BccClientConfiguration();

            boolean validSettings = false;
            if (StringUtils.equalsIgnoreCase(proxy.getProxyType(), CloudAccountConstants.ProxyType.Http.name())) {
                config.setProxyHost(proxy.getProxyIp());
                config.setProxyPort(Integer.parseInt(proxy.getProxyPort()));
                validSettings = true;
            } else if (StringUtils.equalsIgnoreCase(proxy.getProxyType(), CloudAccountConstants.ProxyType.Https.name())) {
                config.setProxyHost(proxy.getProxyIp());
                config.setProxyPort(Integer.parseInt(proxy.getProxyPort()));
                //设置用户名和密码
                config.setProxyUsername(proxy.getProxyName());
                config.setProxyPassword(proxy.getProxyPassword());
                validSettings = true;
            }
            if(!validSettings){
                return null;
            }
            return config;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
