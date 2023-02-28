package com.hummer.common.core.dto;

/**
 * @author harris
 */
public class TaskRuleDTO {

    private RuleDTO ruleDTO;

    private ServerRuleDTO serverRuleDTO;

    private K8sRuleDTO k8sRuleDTO;

    private ConfigRuleDTO configRuleDTO;

    private ImageRuleDTO imageRuleDTO;

    private CodeRuleDTO codeRuleDTO;

    private FsRuleDTO fsRuleDTO;

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

    public K8sRuleDTO getK8sRuleDTO() {
        return k8sRuleDTO;
    }

    public void setK8sRuleDTO(K8sRuleDTO k8sRuleDTO) {
        this.k8sRuleDTO = k8sRuleDTO;
    }

    public ConfigRuleDTO getConfigRuleDTO() {
        return configRuleDTO;
    }

    public void setConfigRuleDTO(ConfigRuleDTO configRuleDTO) {
        this.configRuleDTO = configRuleDTO;
    }

    public FsRuleDTO getFsRuleDTO() {
        return fsRuleDTO;
    }

    public void setFsRuleDTO(FsRuleDTO fsRuleDTO) {
        this.fsRuleDTO = fsRuleDTO;
    }
}
