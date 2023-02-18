package com.hummer.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.hummer.common.security.annotation.EnableCustomConfig;
import com.hummer.common.security.annotation.EnableHummerFeignClients;
import com.hummer.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 混合云模块
 *
 * @author harris1943
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableHummerFeignClients
@SpringBootApplication
public class CloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
        System.out.println("{HTTP/1.1, (http/1.1)}{0.0.0.0:9400}");
        System.out.println("(♥◠‿◠)ﾉﾞ  混合云模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗ ██████╗ ██╗███████╗██╗  ██╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗██╔══██╗██║██╔════╝██║ ██╔╝\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝██████╔╝██║███████╗█████╔╝\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗██╔══██╗██║╚════██║██╔═██╗\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║██║  ██║██║███████║██║  ██╗\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚══════╝╚═╝  ╚═╝");
    }
}
