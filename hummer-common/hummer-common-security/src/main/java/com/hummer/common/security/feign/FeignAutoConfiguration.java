package com.hummer.common.security.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

/**
 * Feign 配置注册
 *
 * @author harris1943
 **/
@Configuration
public class FeignAutoConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
