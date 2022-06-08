package io.hummerrisk.dto;

import io.hummerrisk.base.domain.RuleTag;

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
