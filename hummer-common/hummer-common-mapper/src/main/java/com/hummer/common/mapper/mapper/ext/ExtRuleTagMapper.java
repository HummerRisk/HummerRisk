package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.RuleTag;
import com.hummer.common.mapper.domain.request.rule.RuleTagRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRuleTagMapper {

    List<RuleTag> list(@Param("request") RuleTagRequest request);

}
