package com.hummer.dubbo.customer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableDubbo
public class CustomerDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerDemoApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9400}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  DEMOCustomer 模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  \n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗      ██████╗██╗      ██████╗ ██╗   ██╗██████╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ██╔════╝██║     ██╔═══██╗██║   ██║██╔══██╗\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝    ██║     ██║     ██║   ██║██║   ██║██║  ██║\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ██║     ██║     ██║   ██║██║   ██║██║  ██║\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ╚██████╗███████╗╚██████╔╝╚██████╔╝██████╔╝\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝     ╚═════╝╚══════╝ ╚═════╝  ╚═════╝ ╚═════╝\n");
    }
}

