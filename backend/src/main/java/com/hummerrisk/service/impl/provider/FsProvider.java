package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.Code;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.CodeConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.proxy.code.CodeCredential;
import com.hummerrisk.proxy.code.CodeCredentialRequest;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class FsProvider implements IProvider {

    private static String name = "fsProvider";

    public String getName() {
        return name;
    }

    public String execute(Object... obj) {
        Code code = (Code) obj[0];
        try {
            Proxy proxy;
            String _proxy = "";
            if (code.getProxyId() != null) {
                proxy = (Proxy) obj[1];
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
            } else if (codeCredential != null && codeCredential.getCommit() != null) {
                branch = TrivyConstants.COMMIT + codeCredential.getCommit();
            } else if (codeCredential != null && codeCredential.getTag() != null) {
                branch = TrivyConstants.TAG + codeCredential.getTag();
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + token + TrivyConstants.TRIVY_REPO + TrivyConstants.SKIP_DB_UPDATE + TrivyConstants.OFFLINE_SCAN + TrivyConstants.SECURITY_CHECKS + branch + " " + codeCredential.getUrl() + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(code.getId() + " {code scan}[command]: " + code.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
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
