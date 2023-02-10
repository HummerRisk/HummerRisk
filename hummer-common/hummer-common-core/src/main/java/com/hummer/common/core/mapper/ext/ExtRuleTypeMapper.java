package com.hummer.common.core.mapper.ext;

import java.util.List;
import java.util.Map;

public interface ExtRuleTypeMapper {

    List<Map<String, String>> selectByExample();

    List<Map<String, String>> cloudResourceTypes();

    List<Map<String, String>> vulnResourceTypes();

    String getResourceTypesById(String ruleId);
}
