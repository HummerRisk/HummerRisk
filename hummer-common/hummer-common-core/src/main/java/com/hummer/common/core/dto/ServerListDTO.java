package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.Server;

import java.util.List;

/**
 * @author harris
 */
public class ServerListDTO extends Server {

    private String user;

    private String groupName;

    private Integer riskSum;

    private List<ServerResultDTO> serverResultDTOS;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getRiskSum() {
        return riskSum;
    }

    public void setRiskSum(Integer riskSum) {
        this.riskSum = riskSum;
    }

    public List<ServerResultDTO> getServerResultDTOS() {
        return serverResultDTOS;
    }

    public void setServerResultDTOS(List<ServerResultDTO> serverResultDTOS) {
        this.serverResultDTOS = serverResultDTOS;
    }
}
