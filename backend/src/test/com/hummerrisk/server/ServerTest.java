package com.hummerrisk.server;

import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.Server;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.proxy.server.SshUtil;
import com.hummerrisk.proxy.server.WinRMHelper;
import com.hummerrisk.service.SysListener;
import io.cloudsoft.winrm4j.client.WinRmClientContext;
import io.cloudsoft.winrm4j.winrm.WinRmTool;
import io.cloudsoft.winrm4j.winrm.WinRmToolResponse;

import java.io.IOException;

public class ServerTest {

    @org.junit.Test
    public void Test() throws Exception {
        try{
            SshUtil.loginSsh2(new Server(), new Proxy());//通过ssh连接到服务器
            SysListener.property();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @org.junit.Test
    public void Test2() throws Exception {
        try {
            WinRmClientContext context = WinRmClientContext.newInstance();
            WinRmTool tool = WinRmTool.Builder.builder("xx.xx.xx.xx", "Administrator", "HummerRisk@2022").
                    port(5985).
                    useHttps(false).
                    context(context).
                    build();
            tool.setOperationTimeout(5000L);
            WinRmToolResponse resp = tool.executeCommand("dir");
            context.shutdown();
            System.out.println(resp.getStdOut());
        } catch (Exception e) {
            LogUtil.error(String.format("=======================%s============================", "执行失败") + e.getMessage());
            throw new IOException("Failed to scan：" + e.getMessage());
        }

    }

}
