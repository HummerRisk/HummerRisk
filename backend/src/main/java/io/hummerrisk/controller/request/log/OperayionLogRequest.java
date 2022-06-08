package io.hummerrisk.controller.request.log;

import io.hummerrisk.commons.utils.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

/**
 * harris
 */
public class OperayionLogRequest {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "模糊匹配")
    @FuzzyQuery
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
