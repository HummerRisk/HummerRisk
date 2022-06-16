package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.packageSetting.PackageRuleRequest;
import com.hummerrisk.dto.PackageRuleDTO;

import java.util.List;

public interface ExtPackageRuleMapper {

    List<PackageRuleDTO> ruleList(PackageRuleRequest request);

}
