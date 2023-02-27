package com.hummer.auth;

import com.hummer.common.security.annotation.EnableHrFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 认证授权中心
 *
 * @author harris
 */
@EnableHrFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9200}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "\n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗      █████╗ ██╗   ██╗████████╗██╗  ██╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ██╔══██╗██║   ██║╚══██╔══╝██║  ██║\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝    ███████║██║   ██║   ██║   ███████║\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ██╔══██║██║   ██║   ██║   ██╔══██║\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ██║  ██║╚██████╔╝   ██║   ██║  ██║\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝    ╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚═╝  ╚═╝\n");
    }
}
