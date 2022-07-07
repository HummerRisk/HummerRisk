package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Account;
import com.hummerrisk.base.domain.Image;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.domain.Server;

import java.util.List;


public class AccountTreeDTO {

    private List<Account> cloudAccount;

    private List<Account> vulnAccount;

    private List<Server> serverAccount;

    private List<Image> imageAccount;

    private List<Package> packageAccount;

    public List<Account> getCloudAccount() {
        return cloudAccount;
    }

    public void setCloudAccount(List<Account> cloudAccount) {
        this.cloudAccount = cloudAccount;
    }

    public List<Account> getVulnAccount() {
        return vulnAccount;
    }

    public void setVulnAccount(List<Account> vulnAccount) {
        this.vulnAccount = vulnAccount;
    }

    public List<Server> getServerAccount() {
        return serverAccount;
    }

    public void setServerAccount(List<Server> serverAccount) {
        this.serverAccount = serverAccount;
    }

    public List<Image> getImageAccount() {
        return imageAccount;
    }

    public void setImageAccount(List<Image> imageAccount) {
        this.imageAccount = imageAccount;
    }

    public List<Package> getPackageAccount() {
        return packageAccount;
    }

    public void setPackageAccount(List<Package> packageAccount) {
        this.packageAccount = packageAccount;
    }
}
