package com.hummerrisk.server;

import com.hummerrisk.base.domain.Proxy;
import com.hummerrisk.base.domain.Server;
import com.hummerrisk.proxy.server.SshUtil;
import com.hummerrisk.service.SysListener;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

}
