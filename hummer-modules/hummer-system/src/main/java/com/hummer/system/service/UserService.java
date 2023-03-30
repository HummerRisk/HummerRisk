package com.hummer.system.service;

import com.hummer.common.core.constant.*;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.member.EditPassWordRequest;
import com.hummer.common.core.domain.request.member.UserRequest;
import com.hummer.common.core.dto.UserDTO;
import com.hummer.common.core.dto.UserRoleDTO;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.CodingUtil;
import com.hummer.common.security.service.TokenService;
import com.hummer.common.security.utils.SecurityUtils;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.RoleMapper;
import com.hummer.system.mapper.UserMapper;
import com.hummer.system.mapper.UserRoleMapper;
import com.hummer.system.mapper.ext.ExtUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ExtUserMapper extUserMapper;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private OperationLogService operationLogService;

    public UserDTO insert(UserRequest user) throws Exception {
        checkUserParam(user);
        //
        String id = user.getId();
        User user1 = userMapper.selectByPrimaryKey(id);
        if (user1 != null) {
            HRException.throwException(Translator.get("user_id_already_exists"));
        } else {
            createUser(user);
        }
        List<Map<String, Object>> roles = user.getRoles();
        if (!roles.isEmpty()) {
            insertUserRole(roles, user.getId());
        }
        return getUserDTO(user.getId());
    }

    public User selectUser(String userId, String email) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            UserExample example = new UserExample();
            example.createCriteria().andEmailEqualTo(email);
            List<User> users = userMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(users)) {
                return users.get(0);
            }
        }
        return user;

    }

    @SuppressWarnings("unchecked")
    private void insertUserRole(List<Map<String, Object>> roles, String userId) {
        for (Map<String, Object> map : roles) {
            String role = (String) map.get("id");
            if (StringUtils.equals(role, RoleConstants.ADMIN)) {
                UserRole userRole = new UserRole();
                userRole.setId(UUID.randomUUID().toString());
                userRole.setUserId(userId);
                userRole.setUpdateTime(System.currentTimeMillis());
                userRole.setCreateTime(System.currentTimeMillis());
                userRole.setRoleId(role);
                userRole.setSourceId("adminSourceId");
                userRoleMapper.insertSelective(userRole);
            } else {
                List<String> list = (List<String>) map.get("ids");
                for (String s : list) {
                    UserRole userRole1 = new UserRole();
                    userRole1.setId(UUID.randomUUID().toString());
                    userRole1.setUserId(userId);
                    userRole1.setRoleId(role);
                    userRole1.setUpdateTime(System.currentTimeMillis());
                    userRole1.setCreateTime(System.currentTimeMillis());
                    userRole1.setSourceId(s);
                    userRoleMapper.insertSelective(userRole1);
                }
            }
        }
    }

    private void checkUserParam(User user) {

        if (StringUtils.isBlank(user.getId())) {
            HRException.throwException(Translator.get("user_id_is_null"));
        }

        if (StringUtils.isBlank(user.getName())) {
            HRException.throwException(Translator.get("user_name_is_null"));
        }

        if (StringUtils.isBlank(user.getEmail())) {
            HRException.throwException(Translator.get("user_email_is_null"));
        }
    }

    public void createUser(User userRequest) throws Exception {
        User user = new User();
        BeanUtils.copyBean(user, userRequest);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        // 默认1:启用状态
        user.setStatus(UserStatus.NORMAL);
        user.setSource(UserSource.LOCAL.name());
        // 密码使用 MD5
        user.setPassword(CodingUtil.md5(user.getPassword()));
        checkEmailIsExist(user.getEmail());
        userMapper.insertSelective(user);
        operationLogService.log(tokenService.getLoginUser().getUser(), userRequest.getId(), userRequest.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, "创建用户");
    }

    private void checkEmailIsExist(String email) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andEmailEqualTo(email);
        List<User> userList = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(userList)) {
            HRException.throwException(Translator.get("user_email_already_exists"));
        }
    }

    public UserDTO getUserDTO(String userId) throws Exception {

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        if (StringUtils.equals(user.getStatus(), UserStatus.DISABLED)) {
            throw new Exception();
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyBean(user, userDTO);
        UserRoleDTO userRole = getUserRole(userId);
        userDTO.setUserRoles(Optional.ofNullable(userRole.getUserRoles()).orElse(new ArrayList()));
        userDTO.setRoles(Optional.ofNullable(userRole.getRoles()).orElse(new ArrayList()));
        String token = SecurityUtils.getToken();
        userDTO.setToken(token);
        return userDTO;
    }

    public UserDTO getLoginUser(String userId, List<String> list) throws Exception {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userId).andSourceIn(list);
        if (userMapper.countByExample(example) == 0) {
            return null;
        }
        return getUserDTO(userId);
    }

    public LoginUser getLoginUserByName(String userName) throws Exception {
        LoginUser loginUser = new LoginUser();
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        User exuser = users.get(0);
        if (users.size() == 0 || exuser == null) {
            return new LoginUser();
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyBean(userDTO, exuser);
        UserRoleDTO userRole = getUserRole(exuser.getId());
        userDTO.setUserRoles(Optional.ofNullable(userRole.getUserRoles()).orElse(new ArrayList<>()));
        userDTO.setRoles(Optional.ofNullable(userRole.getRoles()).orElse(new ArrayList<>()));

        loginUser.setUser(userDTO);
        return loginUser;
    }

    public UserDTO getUserDTOByEmail(String email, String... source) throws Exception {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(email);

        if (!CollectionUtils.isEmpty(Arrays.asList(source))) {
            criteria.andSourceIn(Arrays.asList(source));
        }

        List<User> users = userMapper.selectByExample(example);

        if (users.isEmpty()) {
            return null;
        }

        return getUserDTO(users.get(0).getId());
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

    public List<User> getUserList() {
        UserExample example = new UserExample();
        example.setOrderByClause("update_time desc");
        return userMapper.selectByExample(example);
    }

    public List<User> getUserListWithRequest(UserRequest request) {
        return extUserMapper.getUserList(request);
    }

    public void deleteUser(String userId) throws Exception {
        User user = new User();
        BeanUtils.copyBean(user, tokenService.getLoginUser().getUser());
        if (user == null) return;
        if (StringUtils.equals(user.getId(), userId)) {
            HRException.throwException(Translator.get("cannot_delete_current_user"));
        }

        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(userId);
        userRoleMapper.deleteByExample(example);

        userMapper.deleteByPrimaryKey(userId);
        operationLogService.log(tokenService.getLoginUser().getUser(), tokenService.getLoginUser().getUser().getId(), tokenService.getLoginUser().getUser().getName(), ResourceTypeConstants.USER.name(), ResourceOperation.DELETE, "删除用户");
    }

    public void updateUserRole(UserRequest user) {
        String userId = user.getId();
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);

        userRoleMapper.deleteByExample(userRoleExample);
        List<Map<String, Object>> roles = user.getRoles();
        if (!roles.isEmpty()) {
            insertUserRole(roles, user.getId());
        }

        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andEmailEqualTo(user.getEmail());
        criteria.andIdNotEqualTo(user.getId());
        if (userMapper.countByExample(example) > 0) {
            HRException.throwException(Translator.get("user_email_already_exists"));
        }

        user.setUpdateTime(System.currentTimeMillis());
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void updateUser(User user) {
        user.setUpdateTime(System.currentTimeMillis());
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserDTO getUserInfo(String userId) throws Exception {
        return getUserDTO(userId);
    }

    public boolean checkUserPassword(String userId, String password) throws Exception {
        if (StringUtils.isBlank(userId)) {
            HRException.throwException(Translator.get("user_name_is_null"));
        }
        if (StringUtils.isBlank(password)) {
            HRException.throwException(Translator.get("password_is_null"));
        }
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userId).andPasswordEqualTo(CodingUtil.md5(password));
        return userMapper.countByExample(example) > 0;
    }

    public void setLanguage(String lang) {
        if (tokenService.getLoginUser().getUser() != null) {
            User user = new User();
            user.setId(tokenService.getLoginUser().getUser().getId());
            user.setLanguage(lang);
            updateUser(user);
            tokenService.getLoginUser().getUser().setLanguage(lang);
        }
    }

    /*修改当前用户用户密码*/
    private User updateCurrentUserPwd(EditPassWordRequest request) throws Exception {
        String oldPassword = CodingUtil.md5(request.getPassword(), "utf-8");
        String newPassword = request.getNewpassword();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(Objects.requireNonNull(tokenService.getLoginUser().getUser()).getId()).andPasswordEqualTo(oldPassword);
        List<User> users = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            user.setPassword(CodingUtil.md5(newPassword));
            user.setUpdateTime(System.currentTimeMillis());
            return user;
        } else {
            throw new HRException(Translator.get("password_modification_failed"));
        }
    }

    public int updateCurrentUserPassword(EditPassWordRequest request) throws Exception {
        User user = updateCurrentUserPwd(request);
        return extUserMapper.updatePassword(user);
    }

    /*管理员修改用户密码*/
    private User updateUserPwd(EditPassWordRequest request) throws Exception {
        User user = userMapper.selectByPrimaryKey(request.getId());
        String newped = request.getNewpassword();
        user.setPassword(CodingUtil.md5(newped));
        user.setUpdateTime(System.currentTimeMillis());
        operationLogService.log(tokenService.getLoginUser().getUser(), Objects.requireNonNull(tokenService.getLoginUser().getUser()).getId(), tokenService.getLoginUser().getUser().getName(), ResourceTypeConstants.USER.name(), ResourceOperation.UPDATE, "修改密码");
        return user;
    }

    public int updateUserPassword(EditPassWordRequest request) throws Exception {
        User user = updateUserPwd(request);
        return extUserMapper.updatePassword(user);
    }

    public String getDefaultLanguage() {
        final String key = "default.language";
        return extUserMapper.getDefaultLanguage(key);
    }

    public List<User> searchUser(String condition) {
        return extUserMapper.searchUser(condition);
    }

    public User getUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public List<UserDetail> queryTypeByIds(List<String> userIds) {
        return extUserMapper.queryTypeByIds(userIds);
    }
}
