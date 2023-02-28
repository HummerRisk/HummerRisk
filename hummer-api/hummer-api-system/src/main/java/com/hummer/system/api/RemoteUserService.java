package com.hummer.system.api;

import com.hummer.common.core.constant.SecurityConstants;
import com.hummer.common.core.constant.ServiceNameConstants;
import com.hummer.common.core.domain.R;
import com.hummer.system.api.domain.User;
import com.hummer.system.api.factory.RemoteUserFallbackFactory;
import com.hummer.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 *
 * @author hummer
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody User sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
