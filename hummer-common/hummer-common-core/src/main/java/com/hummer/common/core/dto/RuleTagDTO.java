package com.hummer.common.core.dto;


import com.hummer.common.core.domain.RuleTag;

/**
 * @author harris
 */
public class RuleTagDTO extends RuleTag {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
