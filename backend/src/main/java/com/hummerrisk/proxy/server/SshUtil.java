package com.hummerrisk.proxy.server;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.HTTPProxyData;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.Server;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.dto.SshServerDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.io.resource.URLResource;
import org.apache.sshd.common.util.security.SecurityUtils;

import javax.websocket.DeploymentException;
import java.io.*;
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

    /**
     * 登录主机
     * @return 登录成功返回true，否则返回false
     */
    public static Connection login(Server server, Proxy proxy) throws Exception {
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            if(proxy.getProxyIp() != null) {
                HTTPProxyData httpProxyData = new HTTPProxyData(proxy.getProxyIp(), Integer.valueOf(proxy.getProxyPort()), proxy.getProxyName(), proxy.getProxyPassword());
                conn = new Connection(server.getIp(), Integer.valueOf(server.getPort()), httpProxyData);
            } else {
                conn = new Connection(server.getIp(), Integer.valueOf(server.getPort()));
            }

            conn.connect(); // 连接主机

            if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "str")) {
                isAuthenticated = conn.authenticateWithPublicKey(server.getUserName(), server.getPublicKey().toCharArray(), server.getPassword());
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "file")) {
                isAuthenticated = conn.authenticateWithPublicKey(server.getUserName(), server.getPublicKey().toCharArray(), server.getPassword());
            } else if (StringUtils.equalsIgnoreCase(server.getIsPublicKey(), "no")) {
                isAuthenticated = conn.authenticateWithPassword(server.getUserName(), server.getPassword()); // 认证
            }

            if(isAuthenticated){
                LogUtil.info(String.format(tipStr, "ssh2 认证成功"));
            } else {
                LogUtil.error(String.format(tipStr, "ssh2 认证失败"));
            }

        } catch (IOException e) {
            LogUtil.error(String.format(tipStr, "登录失败") + e.getMessage());
            throw new Exception(String.format(tipStr, "登录失败") + e.getMessage());
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        LogUtil.info("ssh2 登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        return conn;
    }

    public static ClientSession loginSshd(Server server, Proxy proxy) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        ClientSession session = null;
        client.start();
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {

            session = client.connect(server.getUserName(), server.getIp(), Integer.valueOf(server.getPort())).verify(50000).getSession();

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
                throw new DeploymentException("Not authenticated within timeout", null);
            }
            if (!auth.isSuccess()) {
                LogUtil.error(String.format(tipStr, "sshd 认证失败"));
                throw new DeploymentException("Failed to authenticate", auth.getException());
            }
            session.close(false);
        } catch (Exception e) {
            LogUtil.error(String.format(tipStr, "登录失败") + e.getMessage());
            throw new Exception(String.format(tipStr, "登录失败") + e.getMessage());
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
    public static String execute(Connection conn, String cmd) throws Exception {
        String result = "";
        Session session = null;
        try {
            if(conn != null){
                session = conn.openSession();  // 打开一个会话
                session.execCommand(cmd);      // 执行命令
                result = processStdout(session.getStdout(), DEFAULT_CHAR_SET);

                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(result)){
                    LogUtil.info("【得到标准输出为空】\n执行的命令如下：\n" + cmd);
                    result = processStdout(session.getStderr(), DEFAULT_CHAR_SET);
                }else{
                    LogUtil.info("【执行命令成功】\n执行的命令如下：\n" + cmd);
                }
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
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return 以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset) throws Exception {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            LogUtil.error("解析脚本出错：" + e.getMessage());
            throw new Exception("解析脚本出错：" + e.getMessage());
        } catch (IOException e) {
            LogUtil.error("解析脚本出错：" + e.getMessage());
            throw new Exception("解析脚本出错：" + e.getMessage());
        }
        return buffer.toString();
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
                session.close(false);


                if (events.contains(ClientChannelEvent.TIMEOUT)) {
                    throw new Exception("【执行命令失败超时】\n执行的命令如下：\n" + cmd + "\n" + ClientChannelEvent.TIMEOUT);
                }
                //如果为得到标准输出为空，说明脚本执行出错了
                if(StringUtils.isBlank(out.toString())){
                    LogUtil.info("【得到标准输出为空】\n执行的命令如下：\n" + cmd);
                    result = out.toString();
                }else{
                    LogUtil.info("【执行命令成功】\n执行的命令如下：\n" + cmd);
                }
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
        return result;
    }

    //将空格替换成‘;’
    public static String replaceSpace(StringBuffer str) {
        return str.toString().replaceAll("\\s", ";");
    }
}

