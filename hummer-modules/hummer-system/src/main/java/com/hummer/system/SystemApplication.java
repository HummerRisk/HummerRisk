package com.hummer.system;

import com.hummer.common.security.annotation.EnableHrFeignClients;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 系统模块
 *
 * @author harris1943
 */
@EnableHrFeignClients
@EnableDubbo
@SpringBootApplication
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9201}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "\n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗     ███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝    ███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝    ╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝\n");
    }
}
