package com.hummerrisk.proxy.server;

public class SshResponse {

    private String stdOutput;
    private String errOutput;
    private int returnCode;

    SshResponse(String stdOutput, String errOutput, int returnCode) {
        this.stdOutput = stdOutput;
        this.errOutput = errOutput;
        this.returnCode = returnCode;
    }

    public String getStdOutput() {
        return stdOutput;
    }

    public String getErrOutput() {
        return errOutput;
    }

    public int getReturnCode() {
        return returnCode;
    }

}


