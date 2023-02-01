package com.hummerrisk.commons.utils;

import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @ClassName ChartData
 * @Author harris
 **/
public class ChartData {

    @ApiModelProperty("X轴")
    private String xAxis;

    @ApiModelProperty("Y轴")
    private BigDecimal yAxis = BigDecimal.ZERO;

    @ApiModelProperty("Y轴-右侧")
    private BigDecimal yAxis2 = BigDecimal.ZERO;

    @ApiModelProperty("series名称")
    private String groupName;

    private BigDecimal markPoint = BigDecimal.ZERO;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("自定义数据")
    private String customId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BigDecimal getMarkPoint() {
        return markPoint;
    }

    public void setMarkPoint(BigDecimal markPoint) {
        this.markPoint = markPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    @JsonProperty("xAxis")
    public String getXAxis() {
        return xAxis;
    }

    public void setXAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    @JsonProperty("yAxis")
    public BigDecimal getYAxis() {
        return yAxis;
    }

    public void setYAxis(BigDecimal yAxis) {
        this.yAxis = yAxis;
    }

    @JsonProperty("yAxis2")
    public BigDecimal getYAxis2() {
        return yAxis2;
    }

    public void setYAxis2(BigDecimal yAxis2) {
        this.yAxis2 = yAxis2;
    }
}
