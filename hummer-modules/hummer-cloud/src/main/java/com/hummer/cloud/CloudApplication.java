package com.hummer.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.hummer.common.security.annotation.EnableCustomConfig;
import com.hummer.common.security.annotation.EnableRyFeignClients;
import com.hummer.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 混合云模块
 *
 * @author harris1943
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class CloudApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  混合云模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗ ██████╗ ██╗███████╗██╗  ██╗\n" +
                "  ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗██╔══██╗██║██╔════╝██║ ██╔╝\n" +
                "  ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝██████╔╝██║███████╗█████╔╝\n" +
                "  ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗██╔══██╗██║╚════██║██╔═██╗\n" +
                "  ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║██║  ██║██║███████║██║  ██╗\n" +
                "  ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝╚══════╝╚═╝  ╚═╝");
    }
}
