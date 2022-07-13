package com.hummerrisk.dto;

/**
 * @author harris
 */
public class TaskRuleDTO {

   private RuleDTO ruleDTO;

   private ServerRuleDTO serverRuleDTO;

   private PackageRuleDTO packageRuleDTO;

   private ImageRuleDTO imageRuleDTO;

    public RuleDTO getRuleDTO() {
        return ruleDTO;
    }

    public void setRuleDTO(RuleDTO ruleDTO) {
        this.ruleDTO = ruleDTO;
    }

    public ServerRuleDTO getServerRuleDTO() {
        return serverRuleDTO;
    }

    public void setServerRuleDTO(ServerRuleDTO serverRuleDTO) {
        this.serverRuleDTO = serverRuleDTO;
    }

    public PackageRuleDTO getPackageRuleDTO() {
        return packageRuleDTO;
    }

    public void setPackageRuleDTO(PackageRuleDTO packageRuleDTO) {
        this.packageRuleDTO = packageRuleDTO;
    }

    public ImageRuleDTO getImageRuleDTO() {
        return imageRuleDTO;
    }

    public void setImageRuleDTO(ImageRuleDTO imageRuleDTO) {
        this.imageRuleDTO = imageRuleDTO;
    }
}
