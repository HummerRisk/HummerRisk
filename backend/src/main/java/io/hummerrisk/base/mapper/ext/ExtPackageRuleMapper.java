package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.controller.request.packageSetting.PackageRuleRequest;
import io.hummerrisk.dto.PackageRuleDTO;

import java.util.List;

public interface ExtPackageRuleMapper {

    List<PackageRuleDTO> ruleList(PackageRuleRequest request);

}
