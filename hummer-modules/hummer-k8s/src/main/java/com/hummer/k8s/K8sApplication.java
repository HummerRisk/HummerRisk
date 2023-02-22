package com.hummer.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.hummer.common.security.annotation.EnableCustomConfig;
import com.hummer.common.security.annotation.EnableHummerFeignClients;
import com.hummer.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 云原生模块
 *
 * @author harris1943
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableHummerFeignClients
@SpringBootApplication
public class K8sApplication {
    public static void main(String[] args) {
        SpringApplication.run(K8sApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9500}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  云原生模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  \n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗     ██╗  ██╗ █████╗ ███████╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ██║ ██╔╝██╔══██╗██╔════╝\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝    █████╔╝ ╚█████╔╝███████╗\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗    ██╔═██╗ ██╔══██╗╚════██║\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ██║  ██╗╚█████╔╝███████║\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝    ╚═╝  ╚═╝ ╚════╝ ╚══════╝");
    }
}
