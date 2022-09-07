package com.hummerrisk.dto;

/**
 * @author harris
 */
public class TaskRuleDTO {

   private RuleDTO ruleDTO;

   private ServerRuleDTO serverRuleDTO;

   private ImageRuleDTO imageRuleDTO;

    private CodeRuleDTO codeRuleDTO;

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

    public ImageRuleDTO getImageRuleDTO() {
        return imageRuleDTO;
    }

    public void setImageRuleDTO(ImageRuleDTO imageRuleDTO) {
        this.imageRuleDTO = imageRuleDTO;
    }

    public CodeRuleDTO getCodeRuleDTO() {
        return codeRuleDTO;
    }

    public void setCodeRuleDTO(CodeRuleDTO codeRuleDTO) {
        this.codeRuleDTO = codeRuleDTO;
    }
}
