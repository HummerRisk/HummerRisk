package com.hummer.common.core.proxy.server;

import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.utils.LogUtil;
import io.cloudsoft.winrm4j.client.WinRmClientContext;
import io.cloudsoft.winrm4j.winrm.WinRmTool;
import io.cloudsoft.winrm4j.winrm.WinRmToolResponse;

import java.io.IOException;

public class WinRMHelper {

    private String ip;

    private String username;

    private String password;
    public static final int DEFAULT_PORT = 5985;

    private static String tipStr = "=======================%s============================";

    public WinRMHelper(final String ip, final String username, final String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    public static String execute(Server server, final String command) throws Exception {
        try {
            WinRmClientContext context = WinRmClientContext.newInstance();
            WinRmTool tool = WinRmTool.Builder.builder(server.getIp(), server.getUserName(), server.getPassword()).
                    port(DEFAULT_PORT).
                    useHttps(false).
                    context(context).
                    build();
            tool.setOperationTimeout(5000L);
            //executeCommand(List<String>) execute a list of commands concatenated with &
            //executePs(String) execute a PowerShell command with the native windows command
            //executePs(List<String>) execute a list of PowerShell commands
            WinRmToolResponse resp = tool.executePs(command);
            context.shutdown();
            return resp.getStdOut();
        } catch (Exception e) {
            LogUtil.error(String.format(tipStr, "执行失败") + e.getMessage());
            throw new IOException("Failed to scan：" + e.getMessage());
        }
    }

    public static void validateWindows(Server server, Proxy proxy) throws Exception {
        try {
            execute(server, "dir");
        } catch (Exception e) {
            LogUtil.error(String.format(tipStr, "登录失败") + e.getMessage());
            throw new IOException("Failed to authenticate：" + e.getMessage());
        }
    }

}
