package com.hummer.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 *
 * @author hummer
 */
@Configuration
public class WebSecurityConfig {

    protected void configure(HttpSecurity http) throws Exception {
        //super.configure( http );
        //loginPage  自定义配置页面
        //loginProcessinUrl登录拦截的URL   security有一个自带的login页面 如果需要自定义登陆页面则重写
        //successForwardUrl  成功登录之后跳转
        http.formLogin()
                .loginPage("/index")
                .loginProcessingUrl("/login")
                .successForwardUrl("/")
                .and()
                //permitAll不饶过安全验证
                //定义哪些请求需要Url需要被保护
                .csrf()
                .disable();
    }
}
