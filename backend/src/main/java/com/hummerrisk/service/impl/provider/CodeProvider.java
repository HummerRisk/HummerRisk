package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.Code;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.ScanSetting;
import com.hummerrisk.commons.constants.CodeConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.dto.ResultDTO;
import com.hummerrisk.proxy.code.CodeCredential;
import com.hummerrisk.proxy.code.CodeCredentialRequest;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class CodeProvider implements IProvider {

    private static String name = "codeProvider";

    public String getName() {
        return name;
    }

    public ResultDTO execute(Object... obj) throws Exception {
        Code code = (Code) obj[0];
        try {
            String _proxy = "";
            if (code.getProxyId() != null) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            CodeCredentialRequest codeRequest = new CodeCredentialRequest();
            codeRequest.setCredential(code.getCredential());
            CodeCredential codeCredential = codeRequest.getCodeClient();
            String token = "", branch = "";
            if (codeCredential != null && codeCredential.getToken() != null) {
                if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITHUB_TOKEN)) {
                    token = "export GITHUB_TOKEN=" + codeCredential.getToken() + "\n";
                } else if (StringUtils.equals(code.getPluginIcon(), CodeConstants.GITLAB_TOKEN)) {
                    token = "export GITLAB_TOKEN=" + codeCredential.getToken() + "\n";
                }
            }
            if (codeCredential != null && codeCredential.getBranch() != null) {
                branch = TrivyConstants.BRANCH + codeCredential.getBranch();
            } else if (codeCredential != null && codeCredential.getTag() != null) {
                branch = TrivyConstants.TAG + codeCredential.getTag();
            } else if (codeCredential != null && codeCredential.getCommit() != null) {
                branch = TrivyConstants.COMMIT + codeCredential.getCommit();
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
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + token + TrivyConstants.TRIVY_REPO + str + branch + " " + codeCredential.getUrl() + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(code.getId() + " {code scan}[command]: " + code.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
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

}
