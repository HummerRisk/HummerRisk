package com.hummerrisk.service.impl.provider;

import com.hummerrisk.base.domain.Image;
import com.hummerrisk.base.domain.ImageRepo;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.constants.ImageConstants;
import com.hummerrisk.commons.constants.TrivyConstants;
import com.hummerrisk.commons.utils.CommandUtils;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.ProxyUtil;
import com.hummerrisk.service.impl.HummerPlugin;
import com.hummerrisk.service.impl.IProvider;
import org.apache.commons.lang3.StringUtils;

@HummerPlugin
public class ImageProvider implements IProvider {

    private static String name = "imageProvider";

    public String getName() {
        return name;
    }

    public String execute(Object ...obj) {
        Image image = (Image) obj[0];
        try {
            String _proxy = "";
            String dockerLogin = "";
            if(image.getIsProxy()!=null && image.getIsProxy()) {
                Proxy proxy = (Proxy) obj[1];
                _proxy = ProxyUtil.isProxy(proxy);
            }
            if(image.getRepoId()!=null) {
                ImageRepo imageRepo = (ImageRepo) obj[2];
                String repo = imageRepo.getRepo().replace("https://", "").replace("http://", "");
                if(repo.endsWith("/")){
                    repo = repo.substring(0,repo.length()-1);
                }
                dockerLogin = "docker login " + repo + " " + "-u " + imageRepo.getUserName() + " -p " + imageRepo.getPassword() + "\n";
            }
            String fileName = "";
            if (StringUtils.equalsIgnoreCase("image", image.getType()) || StringUtils.equalsIgnoreCase("repo", image.getType())) {
                fileName = image.getImageUrl() + ":" + image.getImageTag();
            } else {
                fileName = TrivyConstants.INPUT + ImageConstants.DEFAULT_BASE_DIR + image.getPath();
            }
            CommandUtils.commonExecCmdWithResult(TrivyConstants.TRIVY_RM + TrivyConstants.TRIVY_JSON, TrivyConstants.DEFAULT_BASE_DIR);
            String command = _proxy + dockerLogin + TrivyConstants.TRIVY_IMAGE + TrivyConstants.TRIVY_SKIP + fileName + TrivyConstants.TRIVY_TYPE + TrivyConstants.DEFAULT_BASE_DIR + TrivyConstants.TRIVY_JSON;
            LogUtil.info(image.getId() + " {k8sImage}[command]: " + image.getName() + "   " + command);
            String resultStr = CommandUtils.commonExecCmdWithResult(command, TrivyConstants.DEFAULT_BASE_DIR);
            if(resultStr.contains("ERROR") || resultStr.contains("error")) {
                throw new Exception(resultStr);
            }
            return resultStr;
        } catch (Exception e) {
            return "";
        }
    }

    public String dockerLogin(Object obj) {
        try {
            ImageRepo imageRepo = (ImageRepo) obj;
            String repo = imageRepo.getRepo().replace("https://", "").replace("http://", "");
            if (repo.endsWith("/")) {
                repo = repo.substring(0, repo.length()-1);
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
            return "";
        }
    }

}
