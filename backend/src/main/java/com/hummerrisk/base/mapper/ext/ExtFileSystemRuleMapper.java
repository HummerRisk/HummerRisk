package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.fs.FsRuleRequest;
import com.hummerrisk.dto.FsRuleDTO;

import java.util.List;

public interface ExtFileSystemRuleMapper {

    List<FsRuleDTO> ruleList(FsRuleRequest request);

}
