package com.hummerrisk.controller;

import com.hummerrisk.base.domain.RuleTag;
import com.hummerrisk.service.RuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "标签")
@RestController
@RequestMapping("tag")
public class TagController {

    @Resource
    private RuleService ruleService;

    @ApiOperation(value = "规则标签列表")
    @GetMapping(value = "rule/list")
    public Object listRuleTags() throws Exception {
        return ruleService.getRuleTags();
    }

    @ApiOperation(value = "新增规则标签")
    @RequestMapping(value = "rule/save")
    public Object saveRuleTag(@RequestBody RuleTag ruleTag) {
        return ruleService.saveRuleTag(ruleTag);
    }

    @ApiOperation(value = "修改规则标签")
    @RequestMapping(value = "rule/update")
    public Object updateRuleTag(@RequestBody RuleTag ruleTag) {
        return ruleService.updateRuleTag(ruleTag);
    }

    @ApiOperation(value = "删除规则标签")
    @GetMapping(value = "rule/delete/{tagkey}")
    public Object deleteRuleTags(@PathVariable String tagkey) throws Exception {
        return ruleService.deleteRuleTagByTagKey(tagkey);
    }
}
