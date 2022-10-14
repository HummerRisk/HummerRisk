package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.FileSystem;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.FileSystemConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;

@HummerPlugin
public class FsProvider implements IProvider {

    private static String name = "fsProvider";

    public String getName() {
        return name;
    }

    public String execute(Object... obj) {
        FileSystem fileSystem = (FileSystem) obj[0];
        try {
            Proxy proxy;
            String _proxy = "";
            if (fileSystem.getProxyId() != null) {
                proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, fileSystem.getDir());
            String command = _proxy + TrivyConstants.TRIVY_FS + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.OFFLINE_SCAN + TrivyConstants.SECURITY_CHECKS + FileSystemConstants.DEFAULT_BASE_DIR + fileSystem.getPath() + TrivyConstants.TRIVY_TYPE + fileSystem.getDir() + TrivyConstants.TRIVY_JSON;
            LogUtil.info(fileSystem.getId() + " {fileSystem scan}[command]: " + fileSystem.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, fileSystem.getDir());
            if (resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            return "";
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
