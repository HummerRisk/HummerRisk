package com.hummer.common.core.dto;


import com.hummer.common.core.domain.ServerLynisResultDetail;

import java.util.List;

/**
 * @author harris
 */
public class ServerLynisResultDetailDTO extends ServerLynisResultDetail {

    private Integer resultSum;

    private Integer riskSum;

    public Integer getResultSum() {
        return resultSum;
    }

    public void setResultSum(Integer resultSum) {
        this.resultSum = resultSum;
    }

    public Integer getRiskSum() {
        return riskSum;
    }

    public void setRiskSum(Integer riskSum) {
        this.riskSum = riskSum;
    }

    private List<ServerLynisResultDetail> details;

    public List<ServerLynisResultDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ServerLynisResultDetail> details) {
        this.details = details;
    }
}
