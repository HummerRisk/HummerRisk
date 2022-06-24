package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.image.ImageRuleRequest;
import com.hummerrisk.dto.ImageRuleDTO;

import java.util.List;

public interface ExtImageRuleMapper {

    List<ImageRuleDTO> ruleList(ImageRuleRequest request);

}
