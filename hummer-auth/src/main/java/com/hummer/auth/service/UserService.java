package com.hummer.auth.service;

import com.hummer.auth.mapper.OperationLogMapper;
import com.hummer.auth.mapper.UserMapper;
import com.hummer.auth.mapper.ext.ExtUserMapper;
import com.hummer.common.core.domain.OperationLog;
import com.hummer.common.core.domain.UserExample;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.system.api.domain.User;
import com.hummer.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExtUserMapper extUserMapper;
    @Autowired
    private OperationLogMapper operationLogMapper;

    public User getUserById(String id) throws Exception {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    public LoginUser getLoginUserByName(String userName) throws Exception {
        LoginUser loginUser = new LoginUser();
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        loginUser.setUser(users.get(0));
        return loginUser;
    }

    public OperationLog createOperationLog(com.hummer.system.api.domain.User user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip) {
        OperationLog operationLog = new OperationLog();
        operationLog.setId(UUIDUtil.newUUID());
        operationLog.setResourceId(resourceId);
        operationLog.setResourceName(resourceName);
        if (user == null) {
            operationLog.setResourceUserId(SysLoginService.SystemUserConstants.getUserId());
            operationLog.setResourceUserName(SysLoginService.SystemUserConstants.getUser().getName());
        } else {
            operationLog.setResourceUserId(user.getId());
            operationLog.setResourceUserName(user.getName() + " [" + user.getEmail() + "]");
        }
        operationLog.setResourceType(resourceType);
        operationLog.setOperation(operation);
        operationLog.setMessage(message);
        operationLog.setSourceIp(ip);
        operationLog.setTime(System.currentTimeMillis());
        operationLogMapper.insertSelective(operationLog);
        return operationLog;
    }

    public String getDefaultLanguage() {
        final String key = "default.language";
        return extUserMapper.getDefaultLanguage(key);
    }

}
