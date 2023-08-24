package com.hummer.common.core.constant;

/**
 * @author harris
 */
public enum TaskEnum {
    cloudAccount("cloudAccount"), cloudProject("cloudProject"), rule("rule"), tag("tag"), group("group");

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    TaskEnum(String type) {
        this.type = type;
    }
}
