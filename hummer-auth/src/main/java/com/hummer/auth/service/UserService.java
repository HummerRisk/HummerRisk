package com.hummer.auth.service;

import com.hummer.auth.mapper.OperationLogMapper;
import com.hummer.auth.mapper.RoleMapper;
import com.hummer.auth.mapper.UserMapper;
import com.hummer.auth.mapper.UserRoleMapper;
import com.hummer.auth.mapper.ext.ExtUserMapper;
import com.hummer.common.core.domain.*;
import com.hummer.system.api.domain.User;
import com.hummer.common.core.dto.UserDTO;
import com.hummer.common.core.dto.UserRoleDTO;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    public User getUserById(String id) throws Exception {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    public LoginUser getLoginUserByName(String userName) throws Exception {
        LoginUser loginUser = new LoginUser();
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            return null;
        }
        User exuser = users.get(0);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyBean(userDTO, exuser);
        UserRoleDTO userRole = getUserRole(exuser.getId());
        userDTO.setUserRoles(Optional.ofNullable(userRole.getUserRoles()).orElse(new ArrayList<>()));
        userDTO.setRoles(Optional.ofNullable(userRole.getRoles()).orElse(new ArrayList<>()));

        loginUser.setUser(userDTO);
        return loginUser;
    }

    public UserRoleDTO getUserRole(String userId) {
        UserRoleDTO userRoleDTO = new UserRoleDTO();
        //
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoleList = userRoleMapper.selectByExample(userRoleExample);

        if (CollectionUtils.isEmpty(userRoleList)) {
            return userRoleDTO;
        }
        // 设置 user_role
        userRoleDTO.setUserRoles(userRoleList);

        List<String> roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdIn(roleIds);

        List<Role> roleList = roleMapper.selectByExample(roleExample);
        userRoleDTO.setRoles(roleList);

        return userRoleDTO;
    }

    public OperationLog createOperationLog(UserDTO user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip) {
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
