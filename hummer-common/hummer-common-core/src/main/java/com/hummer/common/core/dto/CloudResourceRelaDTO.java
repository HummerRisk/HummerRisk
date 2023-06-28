package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudResourceRela;

public class CloudResourceRelaDTO extends CloudResourceRela {

    private String value;

    private Long x;

    private Long y;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }
}
