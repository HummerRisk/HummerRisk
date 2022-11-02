package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.Image;
import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.ScanSetting;
import com.hummerrisk.commons.constants.ImageConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.dto.ResultDTO;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class ImageProvider implements IProvider {

    private static String name = "imageProvider";

    public String getName() {
        return name;
    }

    public ResultDTO execute(Object... obj) throws Exception {
        Image image = (Image) obj[0];
        try {
            String _proxy = "";
            String dockerLogin = "";
            if (image.getIsProxy() != null && image.getIsProxy()) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            if (image.getRepoId() != null) {
                ImageRepo imageRepo = (ImageRepo) obj[2];
                String repo = imageRepo.getRepo().replace("https://", "").replace("http://", "");
                if (repo.endsWith("/")) {
                    repo = repo.substring(0, repo.length() - 1);
                }
                dockerLogin = "docker login " + repo + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword() + "\n";
            }
            String fileName = "";
            if (StringUtils.equalsIgnoreCase("image", image.getType()) || StringUtils.equalsIgnoreCase("repo", image.getType())) {
                fileName = image.getImageUrl() + ":" + image.getImageTag();
            } else {
                fileName = TrivyConstants.INPUT + ImageConstants.DEFAULT_BASE_DIR + image.getPath();
            }
            ScanSetting scanSetting = (ScanSetting) obj[3];
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
            String command = _proxy + dockerLogin + TrivyConstants.TRIVY_IMAGE + str + fileName + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(image.getId() + " {k8sImage}[command]: " + image.getName() + "   " + command);
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

    public String dockerLogin(Object obj) throws Exception {
        try {
            ImageRepo imageRepo = (ImageRepo) obj;
            String repo = imageRepo.getRepo().replace("https://", "").replace("http://", "");
            if (repo.endsWith("/")) {
                repo = repo.substring(0, repo.length() - 1);
            }
            String dockerLogin = "";
            if (StringUtils.equalsIgnoreCase(imageRepo.getPluginIcon(), "dockerhub.png")) {
                dockerLogin = "docker login " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword();
            } else {
                dockerLogin = "docker login " + repo + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword();
            }
            String resultStr = CommandUtils.commonExecCmdWithResult(dockerLogin, ImageConstants.DEFAULT_BASE_DIR);
            return resultStr;
        } catch (Exception e) {
            throw e;
        }
    }

}
