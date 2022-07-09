package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.task.*;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.QuartzTaskDTO;
import com.hummerrisk.dto.RuleDTO;
import com.hummerrisk.dto.ServerDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
}
