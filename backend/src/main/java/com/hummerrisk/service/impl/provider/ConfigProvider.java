package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.CloudNativeConfig;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;

@HummerPlugin
public class ConfigProvider implements IProvider {

    private static String name = "configProvider";

    public String getName() {
        return name;
    }

    public String execute(Object... obj) throws Exception {
        CloudNativeConfig cloudNativeConfig = (CloudNativeConfig) obj[0];
        try {
            Proxy proxy;
            String _proxy = "";
            if (cloudNativeConfig.getProxyId() != null) {
                proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            CommandUtils.saveAsFile(cloudNativeConfig.getConfigYaml(), TrivyConstants.DEFAULT_BASE_DIR, "trivy.yaml");
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + TrivyConstants.TRIVY_CONFIG + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_YAML + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(cloudNativeConfig.getId() + " {k8sConfig}[command]: " + cloudNativeConfig.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
            if (resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            throw e;
        }
    }

    public String dockerLogin(Object obj) {
        try {
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
