package com.hummerrisk.proxy.server;

import io.cloudsoft.winrm4j.client.WinRmClientContext;
import io.cloudsoft.winrm4j.winrm.WinRmTool;
import io.cloudsoft.winrm4j.winrm.WinRmToolResponse;
import org.apache.http.client.config.AuthSchemes;

public class WinRMHelper {

    private String ip;

    private String username;

    private String password;

    public static final int DEFAULT_PORT = 5985;

    public WinRMHelper(final String ip,final String username,final String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public String execute(final String command) {
        WinRmClientContext context = WinRmClientContext.newInstance();
        WinRmTool tool = WinRmTool.Builder.builder(ip, username, password).setAuthenticationScheme(AuthSchemes.NTLM).port(DEFAULT_PORT).useHttps(false).context(context).build();
        tool.setOperationTimeout(5000L);
        WinRmToolResponse resp = tool.executeCommand(command);
        context.shutdown();
        return resp.getStdOut();
    }

}
