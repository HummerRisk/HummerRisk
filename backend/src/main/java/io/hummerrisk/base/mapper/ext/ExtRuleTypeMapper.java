package io.hummerrisk.base.mapper.ext;

import java.util.List;
import java.util.Map;

public interface ExtRuleTypeMapper {

    List<Map<String, String>> selectByExample();

    String getResourceTypesById(String ruleId);
}
