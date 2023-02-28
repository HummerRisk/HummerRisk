package com.hummer.common.security.interceptor;

import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author hummer
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {
}
