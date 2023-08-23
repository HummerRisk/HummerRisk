package com.hummer.common.core.dto;

import com.hummer.common.core.domain.CloudGroup;
import com.hummer.common.core.domain.CloudGroupLog;

import java.util.List;

public class CloudGroupDTO extends CloudGroup {

    private Integer sum = 0;

    private Integer complianceNum = 0;

    private Integer critical = 0;

    private Integer high = 0;

    private Integer medium = 0;

    private Integer low = 0;

    private List<CloudGroupLog> cloudCloudGroupLogList;

    public List<CloudGroupLog> getCloudCloudGroupLogList() {
        return cloudCloudGroupLogList;
    }

    public void setCloudCloudGroupLogList(List<CloudGroupLog> cloudCloudGroupLogList) {
        this.cloudCloudGroupLogList = cloudCloudGroupLogList;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public Integer getComplianceNum() {
        return complianceNum;
    }

    public void setComplianceNum(Integer complianceNum) {
        this.complianceNum = complianceNum;
    }

    public Integer getCritical() {
        return critical;
    }

    public void setCritical(Integer critical) {
        this.critical = critical;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }
}
