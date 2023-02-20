package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.ImageRuleDTO;
import com.hummer.common.mapper.domain.request.image.ImageRuleRequest;

import java.util.List;

public interface ExtImageRuleMapper {

    List<ImageRuleDTO> ruleList(ImageRuleRequest request);

}
