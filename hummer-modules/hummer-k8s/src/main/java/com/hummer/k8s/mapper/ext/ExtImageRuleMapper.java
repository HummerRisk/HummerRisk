package com.hummer.k8s.mapper.ext;

import com.hummer.common.core.dto.ImageRuleDTO;
import com.hummer.common.core.domain.request.image.ImageRuleRequest;

import java.util.List;

public interface ExtImageRuleMapper {

    List<ImageRuleDTO> ruleList(ImageRuleRequest request);

}
