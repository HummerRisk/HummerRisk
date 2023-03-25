package com.hummer.cloud.mapper.ext;

import java.util.List;
import java.util.Map;

public interface ExtRuleTypeMapper {

    List<Map<String, String>> selectByExample();

    List<Map<String, String>> cloudResourceTypes();

    String getResourceTypesById(String ruleId);
}
