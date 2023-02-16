package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.CloudNativeConfig;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.ScanSetting;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.dto.ResultDTO;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class ConfigProvider implements IProvider {

    private static String name = "configProvider";

    public String getName() {
        return name;
    }

    public ResultDTO execute(Object... obj) throws Exception {
        CloudNativeConfig cloudNativeConfig = (CloudNativeConfig) obj[0];
        try {
            String _proxy = "";
            if (cloudNativeConfig.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_POLICY_UPDATE;
            }
            if(scanSetting.getIgnoreUnfixed() != null && StringUtils.equalsIgnoreCase(scanSetting.getIgnoreUnfixed(), "true")) {
                str = str + TrivyConstants.UNFIXED;
            }
            if(scanSetting.getSecurityChecks() != null) {
                str = str + TrivyConstants.SECURITY_CHECKS + scanSetting.getSecurityChecks();
            } else {
                str = str + TrivyConstants.SECURITY_CHECKS_DEFAULT;
            }
            if(scanSetting.getOfflineScan() != null && StringUtils.equalsIgnoreCase(scanSetting.getOfflineScan(), "true")) {
                str = str + TrivyConstants.OFFLINE_SCAN;
            }
            CommandUtils.saveAsFile(cloudNativeConfig.getConfigYaml(), TrivyConstants.DEFAULT_BASE_DIR, "trivy.yaml", false);
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + TrivyConstants.TRIVY_CONFIG + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_YAML + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON + TrivyConstants.TRIVY_SERVER;
            LogUtil.info(cloudNativeConfig.getId() + " {k8sConfig}[command]: " + cloudNativeConfig.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);

            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
        } catch (Exception e) {
            throw e;
        }
    }

}
