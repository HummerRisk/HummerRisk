package com.hummer.common.core.utils;

import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.constant.CloudAccountConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author harris
 */
public class ProxyUtil {

    public static String isProxy(Proxy proxy){
        String _proxy = "";
        String proxyType = proxy.getProxyType();
        String proxyIp = proxy.getProxyIp();
        String proxyPort = proxy.getProxyPort();
        String proxyName = proxy.getProxyName();
        String proxyPassword = proxy.getProxyPassword();
        if (StringUtils.isNotEmpty(proxyType)) {
            if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Http.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    _proxy = "export http_proxy=http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + ";" + "\n";
                } else {
                    _proxy = "export http_proxy=http://" + proxyIp + ":" + proxyPort + ";" + "\n";
                }
            } else if (StringUtils.equalsIgnoreCase(proxyType, CloudAccountConstants.ProxyType.Https.toString())) {
                if (StringUtils.isNotEmpty(proxyName)) {
                    _proxy = "export https_proxy=http://" + proxyIp + ":" + proxyPassword + "@" + proxyIp + ":" + proxyPort + ";" + "\n";
                } else {
                    _proxy = "export https_proxy=http://" + proxyIp + ":" + proxyPort + ";" + "\n";
                }
            }
        } else {
            _proxy = "unset http_proxy;" + "\n" +
                    "unset https_proxy;" + "\n";
        }
        return _proxy;
    }

}
