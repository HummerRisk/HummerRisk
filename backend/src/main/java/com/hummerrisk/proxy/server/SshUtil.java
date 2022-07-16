package com.hummerrisk.proxy.server;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.HTTPProxyData;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.dto.SshServerDTO;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Calendar;
import java.util.StringTokenizer;



public class SshUtil {

    private static String DEFAULT_CHAR_SET = "UTF-8";
    private static String tipStr = "=======================%s============================";
    private static String splitStr = "=====================================================";

    /**
     * 登录主机
     * @return 登录成功返回true，否则返回false
     */
    public static Connection login(SshServerDTO sshServerDTO) throws Exception {
        boolean isAuthenticated = false;
        Connection conn = null;
        long startTime = Calendar.getInstance().getTimeInMillis();
        try {
            Proxy proxy = sshServerDTO.getProxy();
            if(sshServerDTO.getProxy().getProxyIp() != null) {
                HTTPProxyData httpProxyData = new HTTPProxyData(proxy.getProxyIp(), Integer.valueOf(proxy.getProxyPort()), proxy.getProxyName(), proxy.getProxyPassword());
                conn = new Connection(sshServerDTO.getSshIp(), sshServerDTO.getSshPort(), httpProxyData);
            } else {
                conn = new Connection(sshServerDTO.getSshIp(), sshServerDTO.getSshPort());
            }

            conn.connect(); // 连接主机

            if (sshServerDTO.getIsPublicKey()) {
                isAuthenticated = conn.authenticateWithPublicKey(sshServerDTO.getSshUserName(), sshServerDTO.getPublicKey().toCharArray(), sshServerDTO.getSshPassword());
            } else {
                isAuthenticated = conn.authenticateWithPassword(sshServerDTO.getSshUserName(), sshServerDTO.getSshPassword()); // 认证
            }

            if(isAuthenticated){
                LogUtil.info(String.format(tipStr, "认证成功"));
            } else {
                LogUtil.error(String.format(tipStr, "认证失败"));
                throw new Exception(String.format(tipStr, "认证失败"));
            }

        } catch (IOException e) {
            LogUtil.error(String.format(tipStr, "登录失败"));
            throw new Exception(String.format(tipStr, "登录失败"));
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        LogUtil.info("登录用时: " + (endTime - startTime)/1000.0 + "s\n" + splitStr);
        return conn;
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
//        StringBuffer str = new StringBuffer(result);
//        result = replaceSpace(str);
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

    //将空格替换成‘;’
    public static String replaceSpace(StringBuffer str) {
        return str.toString().replaceAll("\\s", ";");
    }
}

