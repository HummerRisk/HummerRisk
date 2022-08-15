package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public enum TaskEnum {
    cloudAccount("cloudAccount"), vulnAccount("vulnAccount"), serverAccount("serverAccount"),
    imageAccount("imageAccount"), packageAccount("packageAccount"), k8sAccount("k8sAccount"),
    codeAccount("codeAccount"), rule("rule"), tag("tag"), group("group");

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
