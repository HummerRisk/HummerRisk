package com.hummer.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 定时任务
 *
 * @author harris1943
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class JobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9202}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  定时任务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  \n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗          ██╗ ██████╗ ██████╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗         ██║██╔═══██╗██╔══██╗\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝         ██║██║   ██║██████╔╝\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ██   ██║██║   ██║██╔══██╗\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ╚█████╔╝╚██████╔╝██████╔╝\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝     ╚════╝  ╚═════╝ ╚═════╝\n");
    }
}
