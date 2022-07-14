package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.AccountExample;
import com.hummerrisk.base.domain.ImageExample;
import com.hummerrisk.base.domain.PackageExample;
import com.hummerrisk.base.domain.ServerExample;
import com.hummerrisk.controller.request.task.*;
import com.hummerrisk.dto.*;

import java.util.List;

public interface ExtTaskMapper {

    List<AccountVo> selectAccountByExample(AccountExample example);

    List<AccountVo> selectVulnByExample(AccountExample example);

    List<ServerVo> selectServerByExample(ServerExample example);

    List<ImageVo> selectImageByExample(ImageExample example);

    List<PackageVo> selectPackageByExample(PackageExample example);

    List<RuleVo> ruleList(RuleVo ruleVo);

    List<RuleVo> cloudRuleList(RuleVo ruleVo);

    List<RuleVo> vulnRuleList(RuleVo ruleVo);

    List<RuleVo> serverRuleList(RuleVo ruleVo);

    List<RuleVo> imageRuleList(RuleVo ruleVo);

    List<RuleVo> packageRuleList(RuleVo ruleVo);

    List<RuleVo> ruleTagList(RuleVo ruleVo);

    List<RuleVo> ruleGroupList(RuleVo ruleVo);

    RuleDTO cloudDetailRule(RuleVo ruleVo);

    RuleDTO vulnDetailRule(RuleVo ruleVo);

    ServerRuleDTO serverDetailRule(RuleVo ruleVo);

    ImageRuleDTO imageDetailRule(RuleVo ruleVo);

    PackageRuleDTO packageDetailRule(RuleVo ruleVo);

    List<TaskTagGroupDTO> detailTag(RuleVo ruleVo);

    List<TaskTagGroupDTO> detailGroup(RuleVo ruleVo);

    List<TaskVo> taskList(TaskRequest request);
}
