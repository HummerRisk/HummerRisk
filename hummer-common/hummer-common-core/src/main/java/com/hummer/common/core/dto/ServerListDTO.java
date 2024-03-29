package com.hummer.common.core.dto;


import com.hummer.common.core.domain.Server;
import com.hummer.common.core.domain.ServerLynisResultWithBLOBs;

import java.util.List;

/**
 * @author harris
 */
public class ServerListDTO extends Server {

    private String user;

    private String groupName;

    private Integer riskSum;

    private String resultStatus;

    private ServerLynisResultWithBLOBs serverLynisResult;

    private List<ServerLynisResultDetailDTO> serverLynisResultDetails;

    private List<ServerLynisResultDetailDTO> serverLynisWarnings;

    private List<ServerLynisResultDetailDTO> serverLynisSuggestions;

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

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public ServerLynisResultWithBLOBs getServerLynisResult() {
        return serverLynisResult;
    }

    public void setServerLynisResult(ServerLynisResultWithBLOBs serverLynisResult) {
        this.serverLynisResult = serverLynisResult;
    }

    public List<ServerLynisResultDetailDTO> getServerLynisResultDetails() {
        return serverLynisResultDetails;
    }

    public void setServerLynisResultDetails(List<ServerLynisResultDetailDTO> serverLynisResultDetails) {
        this.serverLynisResultDetails = serverLynisResultDetails;
    }

    public List<ServerLynisResultDetailDTO> getServerLynisWarnings() {
        return serverLynisWarnings;
    }

    public void setServerLynisWarnings(List<ServerLynisResultDetailDTO> serverLynisWarnings) {
        this.serverLynisWarnings = serverLynisWarnings;
    }

    public List<ServerLynisResultDetailDTO> getServerLynisSuggestions() {
        return serverLynisSuggestions;
    }

    public void setServerLynisSuggestions(List<ServerLynisResultDetailDTO> serverLynisSuggestions) {
        this.serverLynisSuggestions = serverLynisSuggestions;
    }
}
