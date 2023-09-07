package com.hummer.system.dto;

import com.hummer.common.core.domain.Server;
import com.hummer.common.core.domain.ServerLynisResult;
import com.hummer.common.core.domain.ServerLynisResultDetail;
import com.hummer.common.core.domain.ServerResult;

import java.util.List;

public class ServerDTO {

    private Server server;

    private ServerLynisResult serverLynisResult;

    private List<ServerLynisResultDetail> serverLynisResultDetailList;

    private List<ServerResult> serverResultList;

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public ServerLynisResult getServerLynisResult() {
        return serverLynisResult;
    }

    public void setServerLynisResult(ServerLynisResult serverLynisResult) {
        this.serverLynisResult = serverLynisResult;
    }

    public List<ServerLynisResultDetail> getServerLynisResultDetailList() {
        return serverLynisResultDetailList;
    }

    public void setServerLynisResultDetailList(List<ServerLynisResultDetail> serverLynisResultDetailList) {
        this.serverLynisResultDetailList = serverLynisResultDetailList;
    }

    public List<ServerResult> getServerResultList() {
        return serverResultList;
    }

    public void setServerResultList(List<ServerResult> serverResultList) {
        this.serverResultList = serverResultList;
    }
}
