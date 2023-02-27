package com.hummer.common.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import com.hummer.common.security.config.ApplicationConfig;
import com.hummer.common.security.feign.FeignAutoConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 开启线程异步执行
@EnableAsync
// 自动加载类
@Import({ ApplicationConfig.class, FeignAutoConfiguration.class })
public @interface EnableCustomConfig
{

}
