package com.hummerrisk.dto;

import java.util.List;


public class ChartDTO {

    private List<String> xAxis;

    private List<Integer> yAxis;

    public List<String> getXAxis() {
        return xAxis;
    }

    public void setXAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<Integer> getYAxis() {
        return yAxis;
    }

    public void setYAxis(List<Integer> yAxis) {
        this.yAxis = yAxis;
    }
}
