package com.hummerrisk.proxy.server;

import ch.ethz.ssh2.Connection;

public class Mysql {

    /*数据库服务*/
    public static String mysqlUsagerate(String sshIp, String sshUsername, String sshPassword) {
        /*获取mysql的pid*/
        Connection root = null;
        try {
            String pid = "";
            root = SshUtil.login(sshIp, sshUsername, sshPassword, null);//通过ssh连接到服务器
            String execute = SshUtil.execute(root, "netstat -ntpl|grep mysql");//执行命令

            //以下是我业务逻辑 可忽略
            if (!"".equals(execute)) {
                String[] executes = execute.split(";");
                for (int i = 0; i < executes.length; i++) {
                    if (i == 6) {
                        pid = executes[i].split("/")[0];
                    }
                }
                if (!"".equals(pid)) {
                    execute = SshUtil.execute(root, "ps -aux |grep " + pid);//根据pid进程获取状态信息
                    String[] Usagerates = execute.split(";");
                    for (int i = 0; i < Usagerates.length; i++) {
                        if (i == 3) {
                            return Usagerates[i];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (root != null) {
                root.close();
            }
        }
        return null;
    }

}
