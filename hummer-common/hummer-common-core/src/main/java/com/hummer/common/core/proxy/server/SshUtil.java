package com.hummer.common.core.proxy.server;

import com.hummer.common.core.domain.Proxy;
import com.hummer.common.core.domain.Server;
import com.hummer.common.core.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.io.resource.URLResource;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


public class SshUtil {

    private static String DEFAULT_CHAR_SET = "UTF-8";
    private static String tipStr = "=======================%s============================";
    private static String splitStr = "=====================================================";

    public static void validateSshd(Server server, Proxy proxy) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        ClientSession session = null;
        client.start();
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {

            session = client.connect(server.getUserName(), server.getIp(), Integer.valueOf(server.getPort())).verify(5000).getSession();

            if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                // 密钥模式
                URLResource idenreplacedy = new URLResource(Paths.get(server.getPublicKeyPath()).toUri().toURL());
                try (InputStream inputStream = idenreplacedy.openInputStream()) {
                    session.addPublicKeyIdentity(GenericUtils.head(SecurityUtils.loadKeyPairIdentities(session, idenreplacedy, inputStream, (s, resourceKey, retryIndex) -> null)));
                }
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                // 密钥模式
                URLResource idenreplacedy = new URLResource(Paths.get(server.getPublicKeyPath()).toUri().toURL());
                try (InputStream inputStream = idenreplacedy.openInputStream()) {
                    session.addPublicKeyIdentity(GenericUtils.head(SecurityUtils.loadKeyPairIdentities(session, idenreplacedy, inputStream, (s, resourceKey, retryIndex) -> null)));
                }
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "no")) {
                // 密码模式
                session.addPasswordIdentity(server.getPassword());
            }

            AuthFuture auth = session.auth();
            if (!auth.await(5000)) {
                LogUtil.error(String.format(tipStr, "sshd 认证失败"));
                throw new Exception("Not authenticated within timeout", null);
            }
            if (!auth.isSuccess()) {
                LogUtil.error(String.format(tipStr, "sshd 认证失败"));
                throw new Exception("Failed to authenticate", auth.getException());
            }
        } catch (IOException e) {
            LogUtil.error(String.format(tipStr, "sshd登录失败") + e.getMessage());
            throw new IOException("Failed to authenticate：" + e.getMessage());
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        LogUtil.info("sshd 登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        session.close();
    }

    public static ClientSession loginSshd(Server server, Proxy proxy) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        ClientSession session = null;
        client.start();
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {

            session = client.connect(server.getUserName(), server.getIp(), Integer.valueOf(server.getPort())).verify(5000).getSession();

            if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                // 密钥模式
                URLResource idenreplacedy = new URLResource(Paths.get(server.getPublicKeyPath()).toUri().toURL());
                try (InputStream inputStream = idenreplacedy.openInputStream()) {
                    session.addPublicKeyIdentity(GenericUtils.head(SecurityUtils.loadKeyPairIdentities(session, idenreplacedy, inputStream, (s, resourceKey, retryIndex) -> null)));
                }
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                // 密钥模式
                URLResource idenreplacedy = new URLResource(Paths.get(server.getPublicKeyPath()).toUri().toURL());
                try (InputStream inputStream = idenreplacedy.openInputStream()) {
                    session.addPublicKeyIdentity(GenericUtils.head(SecurityUtils.loadKeyPairIdentities(session, idenreplacedy, inputStream, (s, resourceKey, retryIndex) -> null)));
                }
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "no")) {
                // 密码模式
                session.addPasswordIdentity(server.getPassword());
            }

            AuthFuture auth = session.auth();
            if (!auth.await(5000)) {
                LogUtil.error(String.format(tipStr, "sshd 认证失败"));
                throw new Exception("Not authenticated within timeout", null);
            }
            if (!auth.isSuccess()) {
                LogUtil.error(String.format(tipStr, "sshd 认证失败"));
                throw new Exception("Failed to authenticate", auth.getException());
            }
        } catch (Exception e) {
            LogUtil.error(String.format(tipStr, "sshd登录失败") + e.getMessage());
            throw e;
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        LogUtil.info("sshd 登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        return session;
    }

    /**
     * 远程执行shell脚本或者命令
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public static String executeSshd(ClientSession session, String cmd) throws Exception {
        String result = "";
        try {
            if(session != null){
                ChannelExec ce = session.createExecChannel(cmd);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayOutputStream err = new ByteArrayOutputStream();
                ce.setOut(out);
                ce.setErr(err);

                ce.open();
                Set<ClientChannelEvent> events =
                        ce.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(60000));

                if (events.contains(ClientChannelEvent.TIMEOUT)) {
                    throw new Exception("【执行命令失败超时】\n执行的命令如下：\n" + cmd + "\n" + ClientChannelEvent.TIMEOUT);
                }
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(out.toString())){
                    LogUtil.info("【得到标准输出为空】\n执行的命令如下：\n" + cmd);
                } else{
                    LogUtil.info("【执行命令成功】\n执行的命令如下：\n" + cmd);
                }
                result = out.toString();
            }
        } catch (IOException e) {
            LogUtil.error("【执行命令失败】\n执行的命令如下：\n" + cmd + "\n" + e.getMessage());
            throw new Exception("【执行命令失败】\n执行的命令如下：\n" + cmd + "\n" + e.getMessage());
        }
        StringTokenizer pas = new StringTokenizer(result, " ");
        result = "";
        while (pas.hasMoreTokens()) {
            String s = pas.nextToken();
            result = result + s + " ";
        }
        result = result.trim();
        session.close();
        return result;
    }

}

