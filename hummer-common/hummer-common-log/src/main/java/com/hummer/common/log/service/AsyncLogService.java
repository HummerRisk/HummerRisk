package com.hummer.common.log.service;

import com.hummer.common.core.constant.SecurityConstants;
import com.hummer.system.api.RemoteLogService;
import com.hummer.system.api.domain.SysOperLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 异步调用日志服务
 *
 * @author harris1943
 */
@Service
public class AsyncLogService {
    @Resource
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) {
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
