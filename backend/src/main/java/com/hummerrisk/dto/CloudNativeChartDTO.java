package com.hummerrisk.dto;

import java.util.List;


public class CloudNativeChartDTO {

    private List<String> xAxis;

    private List<Integer> yAxis;

    public List<String> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<Integer> getyAxis() {
        return yAxis;
    }

    public void setyAxis(List<Integer> yAxis) {
        this.yAxis = yAxis;
    }
}
