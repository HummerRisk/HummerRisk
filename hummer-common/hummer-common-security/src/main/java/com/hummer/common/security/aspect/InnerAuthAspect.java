package com.hummer.common.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import com.hummer.common.core.constant.SecurityConstants;
import com.hummer.common.core.exception.InnerAuthException;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.security.annotation.InnerAuth;

/**
 * 内部服务调用验证处理
 *
 * @author harris1943
 */
@Aspect
@Component
public class InnerAuthAspect implements Ordered {

    /**
     * 确保在权限认证aop执行前执行
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
