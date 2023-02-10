package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.Server;

public class ServerValidateDTO {

    private String message;

    private boolean flag;

    private Server server;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
