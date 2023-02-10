package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.request.fs.FsRuleRequest;
import com.hummer.common.core.dto.FsRuleDTO;

import java.util.List;

public interface ExtFileSystemRuleMapper {

    List<FsRuleDTO> ruleList(FsRuleRequest request);

}
