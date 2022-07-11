package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.RuleTag;
import com.hummerrisk.base.domain.ServerResult;
import com.hummerrisk.base.mapper.ext.ExtRuleMapper;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.commons.utils.SelectTag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
