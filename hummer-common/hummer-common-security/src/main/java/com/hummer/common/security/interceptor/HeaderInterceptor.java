package com.hummer.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import com.hummer.common.core.constant.SecurityConstants;
import com.hummer.common.core.context.SecurityContextHolder;
import com.hummer.common.core.utils.ServletUtils;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.security.auth.AuthUtil;
import com.hummer.common.security.utils.SecurityUtils;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author hummer
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
}
