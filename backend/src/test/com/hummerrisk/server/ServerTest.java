package com.hummerrisk.server;

import com.hummerrisk.dto.SshServerDTO;
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
            SshUtil.login(new SshServerDTO());//通过ssh连接到服务器
            SysListener.property();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
