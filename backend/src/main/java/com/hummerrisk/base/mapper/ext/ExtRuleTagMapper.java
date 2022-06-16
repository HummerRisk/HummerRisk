package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.rule.RuleTagRequest;
import com.hummerrisk.base.domain.RuleTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleTagMapper {

    List<RuleTag> list(@Param("request") RuleTagRequest request);

}
