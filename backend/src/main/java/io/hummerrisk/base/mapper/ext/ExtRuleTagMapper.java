package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.base.domain.RuleTag;
import io.hummerrisk.controller.request.rule.RuleTagRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleTagMapper {

    List<RuleTag> list(@Param("request") RuleTagRequest request);

}
