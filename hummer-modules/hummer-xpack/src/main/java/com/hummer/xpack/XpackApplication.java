package com.hummer.xpack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 扩展模块
 *
 * @author harris
 */
@SpringBootApplication
public class XpackApplication {
    public static void main(String[] args) {
        SpringApplication.run(XpackApplication.class, args);
        System.out.println("    {HTTP/1.1, (http/1.1)}{0.0.0.0:9600}");
        System.out.println("    (♥◠‿◠)ﾉﾞ  扩展模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "\n" +
                " ██╗  ██╗██╗   ██╗███╗   ███╗███╗   ███╗███████╗██████╗     ██╗  ██╗██████╗  █████╗  ██████╗██╗  ██╗\n" +
                " ██║  ██║██║   ██║████╗ ████║████╗ ████║██╔════╝██╔══██╗    ╚██╗██╔╝██╔══██╗██╔══██╗██╔════╝██║ ██╔╝\n" +
                " ███████║██║   ██║██╔████╔██║██╔████╔██║█████╗  ██████╔╝     ╚███╔╝ ██████╔╝███████║██║     █████╔╝ \n" +
                " ██╔══██║██║   ██║██║╚██╔╝██║██║╚██╔╝██║██╔══╝  ██╔══██╗     ██╔██╗ ██╔═══╝ ██╔══██║██║     ██╔═██╗ \n" +
                " ██║  ██║╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║███████╗██║  ██║    ██╔╝ ██╗██║     ██║  ██║╚██████╗██║  ██╗\n" +
                " ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝    ╚═╝  ╚═╝╚═╝     ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝\n");
    }
}
