package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.FileSystem;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.ScanSetting;
import com.hummerrisk.commons.constants.FileSystemConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.dto.ResultDTO;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class FsProvider implements IProvider {

    private static String name = "fsProvider";

    public String getName() {
        return name;
    }

    public ResultDTO execute(Object... obj) throws Exception {
        FileSystem fileSystem = (FileSystem) obj[0];
        try {
            String _proxy = "";
            if (fileSystem.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            ScanSetting scanSetting = (ScanSetting) obj[2];
            String str = "";
            if(scanSetting.getSkipDbUpdate() != null && StringUtils.equalsIgnoreCase(scanSetting.getSkipDbUpdate(), "true")) {
                str = str + TrivyConstants.SKIP_DB_UPDATE;
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
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, fileSystem.getDir());
            String command = _proxy + TrivyConstants.TRIVY_FS + str + FileSystemConstants.DEFAULT_BASE_DIR + fileSystem.getPath() + TrivyConstants.TRIVY_TYPE + fileSystem.getDir() + TrivyConstants.TRIVY_JSON;
            LogUtil.info(fileSystem.getId() + " {fileSystem scan}[command]: " + fileSystem.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, fileSystem.getDir());
            if (resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            ResultDTO dto = new ResultDTO();
            dto.setCommand(command);
            dto.setResultStr(resultStr);
            return dto;
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
