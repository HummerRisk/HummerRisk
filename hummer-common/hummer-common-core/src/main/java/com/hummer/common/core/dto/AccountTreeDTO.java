package com.hummer.common.core.dto;

import com.hummer.common.core.domain.request.task.*;

import java.util.List;


public class AccountTreeDTO {

    private List<ProjectVo> projectVoList;

    private List<ServerVo> serverAccount;

    private List<ImageVo> imageAccount;

    private List<CodeVo> codeAccount;

    private List<K8sVo> k8sAccount;

    private List<ConfigVo> configAccount;

    private List<FileSystemVo> fsAccount;

    public List<ProjectVo> getProjectVoList() {
        return projectVoList;
    }

    public void setProjectVoList(List<ProjectVo> projectVoList) {
        this.projectVoList = projectVoList;
    }

    public List<ServerVo> getServerAccount() {
        return serverAccount;
    }

    public void setServerAccount(List<ServerVo> serverAccount) {
        this.serverAccount = serverAccount;
    }

    public List<ImageVo> getImageAccount() {
        return imageAccount;
    }

    public void setImageAccount(List<ImageVo> imageAccount) {
        this.imageAccount = imageAccount;
    }

    public List<CodeVo> getCodeAccount() {
        return codeAccount;
    }

    public void setCodeAccount(List<CodeVo> codeAccount) {
        this.codeAccount = codeAccount;
    }

    public List<K8sVo> getK8sAccount() {
        return k8sAccount;
    }

    public void setK8sAccount(List<K8sVo> k8sAccount) {
        this.k8sAccount = k8sAccount;
    }

    public List<ConfigVo> getConfigAccount() {
        return configAccount;
    }

    public void setConfigAccount(List<ConfigVo> configAccount) {
        this.configAccount = configAccount;
    }

    public List<FileSystemVo> getFsAccount() {
        return fsAccount;
    }

    public void setFsAccount(List<FileSystemVo> fsAccount) {
        this.fsAccount = fsAccount;
    }
}
