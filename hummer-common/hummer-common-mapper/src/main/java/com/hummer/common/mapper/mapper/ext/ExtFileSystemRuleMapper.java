package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.FsRuleDTO;
import com.hummer.common.mapper.domain.request.fs.FsRuleRequest;

import java.util.List;

public interface ExtFileSystemRuleMapper {

    List<FsRuleDTO> ruleList(FsRuleRequest request);

}
