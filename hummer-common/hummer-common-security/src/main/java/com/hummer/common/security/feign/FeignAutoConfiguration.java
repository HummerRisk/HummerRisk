package com.hummer.common.security.feign;

import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

/**
 * Feign 配置注册
 *
 * @author hummer
 **/
@Configuration
public class FeignAutoConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {

            }
        };
    }
}
