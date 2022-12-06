package com.hummerrisk.controller.request.excel;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExportRequest {
    private Map<String, Object> params = new HashMap<>();
    private List<Column> columns = new ArrayList<>();
    private List<Row> data = new ArrayList<>();
    private List<String> accountIds;
    private String groupId;
    private String accountId;
    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Row> getData() {
        return data;
    }

    public void setData(List<Row> data) {
        this.data = data;
    }

    public List<String> getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public static class Column {
        @ApiModelProperty("属性名称")
        private String key;
        @ApiModelProperty("属性表头")
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class Row {
        private String xAxis;
        private BigDecimal yAxis = BigDecimal.ZERO;
        private String groupName;

        public String getxAxis() {
            return xAxis;
        }

        public void setxAxis(String xAxis) {
            this.xAxis = xAxis;
        }

        public BigDecimal getyAxis() {
            return yAxis;
        }

        public void setyAxis(BigDecimal yAxis) {
            this.yAxis = yAxis;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }
}
