package com.hummer.auth.service;

import org.springframework.stereotype.Component;

/**
 * 记录日志方法
 *
 * @author hummer
 */
@Component
public class SysRecordLogService {

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message) {
    }
}
