package com.hummer.system.controller.handler.annotation;


import com.hummer.common.core.constant.I18nConstants;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18n {
    String value() default I18nConstants.LANG_COOKIE_NAME;
}
