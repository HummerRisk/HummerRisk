package com.hummer.common.mapper.handler.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResultHolder {
}
