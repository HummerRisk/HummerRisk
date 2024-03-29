package com.hummer.system.service;

import com.hummer.common.core.domain.OperationLog;
import com.hummer.common.core.domain.OperationLogExample;
import com.hummer.common.core.domain.request.log.OperatorLogRequest;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.domain.User;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.OperationLogMapper;
import com.hummer.system.mapper.ext.ExtOperationLogMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DubboService
@Transactional(rollbackFor = Exception.class)
public class OperationLogService implements IOperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private ExtOperationLogMapper extOperationLogMapper;

    @Override
    public void log(LoginUser user, String resourceId, String resourceName, String resourceType, String operation, String message) {
        String ip = user!=null?user.getIpAddr():"";
        OperationLog operationLog = createOperationLog(user, resourceId, resourceName, resourceType, operation, message, ip);
        operationLogMapper.insertSelective(operationLog);
    }

    @Override
    public OperationLog createOperationLog(LoginUser user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip) {
        OperationLog operationLog = new OperationLog();
        operationLog.setId(UUIDUtil.newUUID());
        operationLog.setResourceId(resourceId);
        operationLog.setResourceName(resourceName);
        if (user == null) {
            operationLog.setResourceUserId(SystemUserConstants.getUserId());
            operationLog.setResourceUserName(SystemUserConstants.getUser().getName() + " [" + SystemUserConstants.getUser().getEmail() + "]");
        } else {
            operationLog.setResourceUserId(user.getUserId());
            operationLog.setResourceUserName(user.getUserName() + " [" + user.getUser().getEmail() + "]");
        }
        operationLog.setResourceType(resourceType);
        operationLog.setOperation(operation);
        operationLog.setMessage(message);
        operationLog.setSourceIp(ip);
        operationLog.setTime(System.currentTimeMillis());
        return operationLog;
    }

    @Override
    public List<OperationLog> selectOperationLog(OperatorLogRequest log) {
        return extOperationLogMapper.selectOperationLog(log);
    }

    @Override
    public List<OperationLog> selectRersourceOperationLog(String resourceId) {
        OperationLogExample example = new OperationLogExample();
        example.createCriteria().andResourceIdEqualTo(resourceId);
        example.setOrderByClause("time desc");
        return operationLogMapper.selectByExample(example);
    }
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
