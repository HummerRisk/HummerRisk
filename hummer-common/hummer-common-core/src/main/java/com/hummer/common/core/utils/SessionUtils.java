package com.hummer.common.core.utils;

import com.hummer.common.core.user.SessionUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.hummer.common.core.constant.SessionConstants.ATTR_USER;

public class SessionUtils {

    public static String getUserId() {
        return Objects.requireNonNull(getUser()).getId();
    }

    public static SessionUser getUser() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            return (SessionUser) session.getAttribute(ATTR_USER);
        } catch (Exception e) {
            return null;
        }
    }

    //
    public static void putUser(SessionUser sessionUser) {
        SecurityUtils.getSubject().getSession().setAttribute(ATTR_USER, sessionUser);
    }

    public static String getRemoteAddress() {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return StringUtils.EMPTY;
            }
            return getRemoteAddress(request);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    public static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public static String getRemoteAddress(HttpServletRequest request) {
        try {
            String ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
            ip = request.getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("Proxy-Client-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (StringUtils.isNotBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
                return ip;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }
}
