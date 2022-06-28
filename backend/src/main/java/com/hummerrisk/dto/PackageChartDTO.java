package com.hummerrisk.dto;

import java.util.List;


public class PackageChartDTO {

    private List<String> xAxis;

    private List<String> yAxis;

    public List<String> getxAxis() {
        return xAxis;
    }

    public void setxAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<String> getyAxis() {
        return yAxis;
    }

    public void setyAxis(List<String> yAxis) {
        this.yAxis = yAxis;
    }
}
