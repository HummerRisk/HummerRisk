package com.hummer.common.security.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.hummer.common.security.annotation.RequiresLogin;
import com.hummer.common.security.annotation.RequiresPermissions;
import com.hummer.common.security.annotation.RequiresRoles;
import com.hummer.common.security.auth.AuthUtil;

/**
 * 基于 Spring Aop 的注解鉴权
 *
 * @author kong
 */
@Aspect
@Component
public class PreAuthorizeAspect {
    /**
     * 构建
     */
    public PreAuthorizeAspect() {
    }

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.hummer.common.security.annotation.RequiresLogin) || "
            + "@annotation(com.hummer.common.security.annotation.RequiresPermissions) || "
            + "@annotation(com.hummer.common.security.annotation.RequiresRoles)";

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }


}
