package com.hummerrisk.dto;

import com.hummerrisk.controller.request.task.AccountVo;
import com.hummerrisk.controller.request.task.ImageVo;
import com.hummerrisk.controller.request.task.PackageVo;
import com.hummerrisk.controller.request.task.ServerVo;

import java.util.List;


public class AccountTreeDTO {

    private List<AccountVo> cloudAccount;

    private List<AccountVo> vulnAccount;

    private List<ServerVo> serverAccount;

    private List<ImageVo> imageAccount;

    private List<PackageVo> packageAccount;

    public List<AccountVo> getCloudAccount() {
        return cloudAccount;
    }

    public void setCloudAccount(List<AccountVo> cloudAccount) {
        this.cloudAccount = cloudAccount;
    }

    public List<AccountVo> getVulnAccount() {
        return vulnAccount;
    }

    public void setVulnAccount(List<AccountVo> vulnAccount) {
        this.vulnAccount = vulnAccount;
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

    public List<PackageVo> getPackageAccount() {
        return packageAccount;
    }

    public void setPackageAccount(List<PackageVo> packageAccount) {
        this.packageAccount = packageAccount;
    }
}
