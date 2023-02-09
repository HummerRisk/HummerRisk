package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Rule;
import com.hummerrisk.base.domain.RuleTag;
import com.hummerrisk.base.mapper.ext.ExtRuleMapper;
import com.hummerrisk.commons.utils.BeanUtils;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.base.domain.SelectTag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author harris
 */
public class RuleDTO extends Rule {

    private String tagKey;

    private String tagName;

    private String taskName;

    private boolean isSaveParam;

    private List<String> tags = new ArrayList<>();

    private List<String> types = new ArrayList<>();

    private List<Integer> ruleSets = new ArrayList<>();

    private List<Integer> inspectionSeports = new ArrayList<>();

    private List<SelectTag> SelectTags = new LinkedList<>();

    private String regions;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Integer> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(List<Integer> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public List<Integer> getInspectionSeports() {
        return inspectionSeports;
    }

    public void setInspectionSeports(List<Integer> inspectionSeports) {
        this.inspectionSeports = inspectionSeports;
    }

    public List<SelectTag> getSelectTags() {
        return SelectTags;
    }

    public void setSelectTags(List<SelectTag> SelectTags) {
        this.SelectTags = SelectTags;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public boolean isSaveParam() {
        return isSaveParam;
    }

    public void setSaveParam(boolean saveParam) {
        isSaveParam = saveParam;
    }

    public RuleDTO fromRules(Rule rule) throws Exception {
        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyBean(ruleDTO, rule);
        List<RuleTag> sfRulesTagList = Objects.requireNonNull(CommonBeanFactory.getBean(ExtRuleMapper.class)).getTagsOfRule(rule.getId());
        for (RuleTag sfRulesTag : sfRulesTagList) {
            ruleDTO.getTags().add(sfRulesTag.getTagKey());
        }
        return ruleDTO;
    }
}
