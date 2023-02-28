package com.hummer.common.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class ChartDTO {

    private List<String> xAxis;

    private List<Integer> yAxis;

    @JsonProperty("xAxis")
    public List<String> getXAxis() {
        return xAxis;
    }

    public void setXAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    @JsonProperty("yAxis")
    public List<Integer> getYAxis() {
        return yAxis;
    }

    public void setYAxis(List<Integer> yAxis) {
        this.yAxis = yAxis;
    }
}
