package com.hummer.common.security.annotation;

import java.lang.annotation.*;

/**
 * 自定义feign注解
 * 添加basePackages路径
 *
 * @author harris1943
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableHummerFeignClients
public @interface EnableHummerFeignClients {
    String[] value() default {};

    String[] basePackages() default {"com.hummer"};

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default {};

    Class<?>[] clients() default {};
}
