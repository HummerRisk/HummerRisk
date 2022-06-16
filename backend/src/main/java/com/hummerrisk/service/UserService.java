package com.hummerrisk.service;

import com.hummerrisk.base.domain.*;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.utils.SessionUtils;
import com.hummerrisk.controller.ResultHolder;
import com.hummerrisk.controller.request.member.AddMemberRequest;
import com.hummerrisk.controller.request.organization.QueryOrgMemberRequest;
import com.hummerrisk.dto.UserDTO;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.mapper.RoleMapper;
import com.hummerrisk.base.mapper.UserMapper;
import com.hummerrisk.base.mapper.UserRoleMapper;
import com.hummerrisk.base.mapper.ext.ExtUserMapper;
import com.hummerrisk.base.mapper.ext.ExtUserRoleMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.user.SessionUser;
import com.hummerrisk.commons.utils.CodingUtil;
import com.hummerrisk.commons.utils.CommonBeanFactory;
import com.hummerrisk.controller.request.LoginRequest;
import com.hummerrisk.controller.request.member.EditPassWordRequest;
import com.hummerrisk.controller.request.member.QueryMemberRequest;
import com.hummerrisk.controller.request.member.UserRequest;
import com.hummerrisk.controller.request.organization.AddOrgMemberRequest;
import com.hummerrisk.dto.UserRoleDTO;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.security.HrUserToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.hummerrisk.commons.constants.SessionConstants.ATTR_USER;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private ExtUserRoleMapper extUserRoleMapper;
    @Resource
    private ExtUserMapper extUserMapper;

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
        BeanUtils.copyProperties(userRequest, user);
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        // 默认1:启用状态
        user.setStatus(UserStatus.NORMAL);
        user.setSource(UserSource.LOCAL.name());
        // 密码使用 MD5
        user.setPassword(CodingUtil.md5(user.getPassword()));
        checkEmailIsExist(user.getEmail());
        userMapper.insertSelective(user);
        OperationLogService.log(SessionUtils.getUser(), userRequest.getId(), userRequest.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, "创建用户");
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

    public UserDTO getUserDTO(String userId) {

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        if (StringUtils.equals(user.getStatus(), UserStatus.DISABLED)) {
            throw new DisabledAccountException();
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        UserRoleDTO userRole = getUserRole(userId);
        userDTO.setUserRoles(Optional.ofNullable(userRole.getUserRoles()).orElse(new ArrayList<>()));
        userDTO.setRoles(Optional.ofNullable(userRole.getRoles()).orElse(new ArrayList<>()));
        return userDTO;
    }

    public UserDTO getLoginUser(String userId, List<String> list) {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(userId).andSourceIn(list);
        if (userMapper.countByExample(example) == 0) {
            return null;
        }
        return getUserDTO(userId);
    }

    public UserDTO getUserDTOByEmail(String email, String... source) {
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

    public List<User> getUserListWithRequest(com.hummerrisk.controller.request.UserRequest request) {
        return extUserMapper.getUserList(request);
    }

    public void deleteUser(String userId) {
        SessionUser user = SessionUtils.getUser();
        if(user == null) return;
        if (StringUtils.equals(user.getId(), userId)) {
            HRException.throwException(Translator.get("cannot_delete_current_user"));
        }

        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(userId);
        userRoleMapper.deleteByExample(example);

        userMapper.deleteByPrimaryKey(userId);
        OperationLogService.log(SessionUtils.getUser(), SessionUtils.getUser().getId(), SessionUtils.getUser().getName(), ResourceTypeConstants.USER.name(), ResourceOperation.DELETE, "删除用户");
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

    public UserDTO getUserInfo(String userId) {
        return getUserDTO(userId);
    }

    public List<User> getMemberList(QueryMemberRequest request) {
        return extUserRoleMapper.getMemberList(request);
    }

    public void addMember(AddMemberRequest request) {
        if (!CollectionUtils.isEmpty(request.getUserIds())) {
            for (String userId : request.getUserIds()) {
                UserRoleExample userRoleExample = new UserRoleExample();
                userRoleExample.createCriteria().andUserIdEqualTo(userId).andSourceIdEqualTo(request.getWorkspaceId());
                List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
                if (!userRoles.isEmpty()) {
                    HRException.throwException(Translator.get("user_already_exists"));
                } else {
                    for (String roleId : request.getRoleIds()) {
                        UserRole userRole = new UserRole();
                        userRole.setRoleId(roleId);
                        userRole.setSourceId(request.getWorkspaceId());
                        userRole.setUserId(userId);
                        userRole.setId(UUID.randomUUID().toString());
                        userRole.setUpdateTime(System.currentTimeMillis());
                        userRole.setCreateTime(System.currentTimeMillis());
                        userRoleMapper.insertSelective(userRole);
                    }
                }
            }
        }
    }

    public void deleteMember(String workspaceId, String userId) {
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andRoleIdLike("%test%")
                .andUserIdEqualTo(userId).andSourceIdEqualTo(workspaceId);

        userRoleMapper.deleteByExample(example);
    }

    public void addOrganizationMember(AddOrgMemberRequest request) {
        if (!CollectionUtils.isEmpty(request.getUserIds())) {
            for (String userId : request.getUserIds()) {
                UserRoleExample userRoleExample = new UserRoleExample();
                userRoleExample.createCriteria().andUserIdEqualTo(userId).andSourceIdEqualTo(request.getOrganizationId());
                List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
                if (!userRoles.isEmpty()) {
                    HRException.throwException(Translator.get("user_already_exists") + ": " + userId);
                } else {
                    for (String roleId : request.getRoleIds()) {
                        UserRole userRole = new UserRole();
                        userRole.setId(UUID.randomUUID().toString());
                        userRole.setRoleId(roleId);
                        userRole.setSourceId(request.getOrganizationId());
                        userRole.setUserId(userId);
                        userRole.setUpdateTime(System.currentTimeMillis());
                        userRole.setCreateTime(System.currentTimeMillis());
                        userRoleMapper.insertSelective(userRole);
                    }
                }
            }
        }
    }

    public void delOrganizationMember(String organizationId, String userId) {
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andRoleIdLike("%org%").andUserIdEqualTo(userId).andSourceIdEqualTo(organizationId);

        userRoleMapper.deleteByExample(userRoleExample);
    }

    public List<User> getOrgMemberList(QueryOrgMemberRequest request) {
        return extUserRoleMapper.getOrgMemberList(request);
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

    /**
     * 查询该组织外的其他用户列表
     */
    public List<User> getBesideOrgMemberList(String orgId) {
        return extUserRoleMapper.getBesideOrgMemberList(orgId);
    }

    public void setLanguage(String lang) {
        if (SessionUtils.getUser() != null) {
            User user = new User();
            user.setId(SessionUtils.getUser().getId());
            user.setLanguage(lang);
            updateUser(user);
            SessionUtils.getUser().setLanguage(lang);
        }
    }

    /*修改当前用户用户密码*/
    private User updateCurrentUserPwd(EditPassWordRequest request) throws Exception {
        String oldPassword = CodingUtil.md5(request.getPassword(), "utf-8");
        String newPassword = request.getNewpassword();
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(Objects.requireNonNull(SessionUtils.getUser()).getId()).andPasswordEqualTo(oldPassword);
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
        OperationLogService.log(SessionUtils.getUser(), Objects.requireNonNull(SessionUtils.getUser()).getId(), SessionUtils.getUser().getName(), ResourceTypeConstants.USER.name(), ResourceOperation.UPDATE, "修改密码");
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

    public ResultHolder login(LoginRequest request) {
        String login = (String) SecurityUtils.getSubject().getSession().getAttribute("authenticate");
        String username = StringUtils.trim(request.getUsername());
        String password = StringUtils.trim(request.getPassword());
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ResultHolder.error("user or password can't be null");
        }

        HrUserToken token = new HrUserToken(username, password, login);
        Subject subject = SecurityUtils.getSubject();

        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                subject.getSession().getAttribute(ATTR_USER);
                OperationLogService.log(SessionUtils.getUser(), Objects.requireNonNull(SessionUtils.getUser()).getId(), SessionUtils.getUser().getName(), ResourceTypeConstants.USER.name(), ResourceOperation.LOGIN, "用户登录");
                // 返回 userDTO
                return ResultHolder.success(subject.getSession().getAttribute("user"));
            } else {
                return ResultHolder.error(Translator.get("login_fail"));
            }
        } catch (ExcessiveAttemptsException e) {
            throw new ExcessiveAttemptsException(Translator.get("excessive_attempts"));
        } catch (LockedAccountException e) {
            throw new LockedAccountException(Translator.get("user_locked"));
        } catch (DisabledAccountException e) {
            throw new DisabledAccountException(Translator.get("user_has_been_disabled"));
        } catch (ExpiredCredentialsException e) {
            throw new ExpiredCredentialsException(Translator.get("user_expires"));
        } catch (AuthenticationException e) {
            throw new AuthenticationException(e.getMessage());
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(Translator.get("not_authorized") + e.getMessage());
        }
    }

    public List<User> searchUser(String condition) {
        return extUserMapper.searchUser(condition);
    }

    public void logout() throws Exception {
        SSOService ssoService = CommonBeanFactory.getBean(SSOService.class);
        if (ssoService != null) {
            ssoService.logout();
        }
    }

    public User getUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public List<UserDetail> queryTypeByIds(List<String> userIds) {
        return extUserMapper.queryTypeByIds(userIds);
    }
}
