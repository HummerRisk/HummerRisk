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
import org.apache.http.client.config.AuthSchemes;

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
        String command1 = "$s=net start|findstr /r \"VNC\"\n" +
                "if($s -like '*VNC*'){\n" +
                "  echo \"HummerError: 检测到 VNC 服务正在运行！\"\n" +
                "}else{\n" +
                "  echo \"HummerSuccess: 无VNC 服务运行，检测正常!\"\n" +
                "}";
        String command2 = "if(Get-EventLog security -newest 100|where {$_.entrytype -eq` \"FailureAudit\"}){\n" +
                "  Get-EventLog security -newest 100|where {$_.entrytype -eq` \"FailureAudit\"}|echo\n" +
                "  echo \"HummerError: 检测异常，出现多次认证失败记录，存在异常登录风险!\"\n" +
                "}else{\n" +
                "  echo \"HummerSuccess: 检测正常!\"\n" +
                "}";
        String command3 = "$t=tasklist|Measure-Object -line|findstr [0-9]\n" +
                "if($t.Trim() -gt 0){\n" +
                "  tasklist|echo\n" +
                "  echo \"HummerSuccess: 检测正常!\"\n" +
                "}else{\n" +
                "  echo \"HummerError: 检测失败，未查询到正在运行的进程信息！\"\n" +
                "}";
        String command4 = "$d=fsutil volume diskfree c:|Measure-Object -line|findstr [0-9]\n" +
                "if($d.Trim() -gt 0){\n" +
                "  fsutil volume diskfree c:|echo\n" +
                "  echo \"HummerSuccess: 检测正常!\"\n" +
                "} else{\n" +
                "  echo \"HummerError: 检测失败，未查询到磁盘信息！\"\n" +
                "}";
        String command5 = "$m=wmic OS get FreePhysicalMemory|findstr [0-9]\n" +
                "# 用户设置一个预留空间，单位 kb $t=2097152\n" +
                "if($m.Trim() -gt $t){\n" +
                "  echo \"HummerSuccess: 内存检测正常!，剩余空间为: $m KB\"\n" +
                "} else{\n" +
                "  echo \"HummerError: 检测失败，内存空间不足！\"\n" +
                "}";
        String command6 = "$m=wmic ComputerSystem get TotalPhysicalMemory |findstr [0-9]\n" +
                "#目标值 Bytes\n" +
                "if($m.Trim() -gt 0){\n" +
                "  echo \"HummerSuccess: 内存检测正常!，内存大小为: $m Bytes\"\n" +
                "} else{\n" +
                "  echo \"HummerError: 检测失败，未检测到内存信息！\"\n" +
                "}";
        String command7 = "$p=netstat -ant|findstr /r '3389'|findstr [0-9]|findstr 'LISTENING'|Measure-Object -line|findstr [0-9]\n" +
                "if($p.Trim() -lt 1){\n" +
                "  echo \"HummerSuccess: 远程访问端口检测正常!\"\n" +
                "} else{\n" +
                "  echo \"HummerError: 检测异常，检测到 3389端口正在运行，建议修改为非常用端口！！\"\n" +
                "}";
        String command8 = "$cpuinfo=wmic CPU get loadpercentage /value|findstr [1-9]\n" +
                "$cpuUse=$cpuinfo.Split('=')|findstr [0-9]\n" +
                "if($cpuUse -and $cpuUse.Trim() -lt 80){\n" +
                "  echo \"HummerSuccess: CPU使用率正常!\"\n" +
                "} else{\n" +
                "  echo \"HummerError: 检测异常，CPU使用率过高，当前为 $cpuUse %!\"\n" +
                "}";
        String command9 = "if(netsh firewall show config){\n" +
                "  netsh firewall show config|echo   \n" +
                "  echo \"HummerSuccess: 防火配置检测正常!\"\n" +
                "}else{\n" +
                "  echo \"HummerError: 检测异常，未查询到防火墙配置信息!\"\n" +
                "}";
        String command10 = "if(netstat -an |findstr /i tcp|findstr /i listen){\n" +
                "  netstat -an |findstr /i tcp|findstr /i listen|echo\n" +
                "  echo \"HummerSuccess: 端口监听检测正常!\"\n" +
                "}else{\n" +
                "  echo \"HummerError: 检测异常，未检测到端口信息!\"\n" +
                "}";
        String command11 = "if(net user){\n" +
                "  net user|echo\n" +
                "  echo \"HummerSuccess: 用户检测正常!\"\n" +
                "}else{\n" +
                "  echo \"HummerError: 检测异常，未检测到用户配置信息!\"\n" +
                "}";
//        excute(command1);
//        excute(command2);
//        excute(command3);
//        excute(command4);
//        excute(command5);
//        excute(command6);
//        excute(command7);
        excute(command8);
//        excute(command9);
//        excute(command10);
//        excute(command11);
    }


    public void excute(String command) throws Exception {
        try {
            WinRmClientContext context = WinRmClientContext.newInstance();
            WinRmTool tool = WinRmTool.Builder.builder("xx.xx.xx.xx", "Administrator", "HummerRisk@2022").
                    authenticationScheme(AuthSchemes.NTLM).
                    port(5985).
                    useHttps(false).
                    context(context).
                    build();
            tool.setOperationTimeout(5000L);
            WinRmToolResponse resp = tool.executePs(command);
            context.shutdown();
            System.out.println("返回结果: ");
            System.out.println(resp.getStdOut());
        } catch (Exception e) {
            throw new IOException("Failed to scan：" + e.getMessage());
        }
    }

}
