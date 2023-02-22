package com.hummer.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 数据迁移启动程序
 *
 * @author harris1943
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FlywayApplication {
    public static void main(String[] args) {

        SpringApplication.run(FlywayApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9600}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  Flyway数据迁移启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "\n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗     ███████╗██╗  ██╗   ██╗██╗    ██╗ █████╗ ██╗   ██╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ██╔════╝██║  ╚██╗ ██╔╝██║    ██║██╔══██╗╚██╗ ██╔╝\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝    █████╗  ██║   ╚████╔╝ ██║ █╗ ██║███████║ ╚████╔╝ \n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ██╔══╝  ██║    ╚██╔╝  ██║███╗██║██╔══██║  ╚██╔╝  \n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ██║     ███████╗██║   ╚███╔███╔╝██║  ██║   ██║   \n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝    ╚═╝     ╚══════╝╚═╝    ╚══╝╚══╝ ╚═╝  ╚═╝   ╚═╝   \n");
    }
}
