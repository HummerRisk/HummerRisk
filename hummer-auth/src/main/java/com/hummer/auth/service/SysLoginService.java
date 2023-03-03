package com.hummer.auth.service;

import com.hummer.common.core.constant.CacheConstants;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.constant.UserConstants;
import com.hummer.common.core.exception.ServiceException;
import com.hummer.common.core.text.Convert;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.core.utils.ip.IpUtils;
import com.hummer.common.redis.service.RedisService;
import com.hummer.system.api.domain.User;
import com.hummer.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 登录校验方法
 *
 * @author hummer
 */
@Component
public class SysLoginService {

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    public LoginUser login(String username, String password) throws Exception {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        LoginUser userInfo = userService.getLoginUserByName(username);

        User user = userInfo.getUser();
        if (StringUtils.equals(user.getStatus(), "0")) {
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (StringUtils.equals(user.getStatus(), "0")) {
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);
        userService.createOperationLog(user, Objects.requireNonNull(user).getId(), user.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.LOGIN, "用户登录", userInfo.getIpAddr());
        return userInfo;
    }

    public void logout(String loginName) {
    }

    class SystemUserConstants extends User {

        private static User user = new User();

        static {
            user.setId("system");
            user.setName("SYSTEM");
        }

        public static User getUser() {
            return user;
        }

        public static String getUserId() {
            return user.getId();
        }

    }


}
