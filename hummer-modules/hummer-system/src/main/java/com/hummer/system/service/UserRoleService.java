package com.hummer.system.service;

import com.hummer.common.core.domain.Role;
import com.hummer.common.core.domain.UserRole;
import com.hummer.common.core.domain.UserRoleExample;
import com.hummer.system.mapper.UserRoleMapper;
import com.hummer.system.mapper.ext.ExtUserRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleService {

    @Resource
    private ExtUserRoleMapper extUserRoleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    public List<Role> getOrganizationMemberRoles(String orgId, String userId) {
        return extUserRoleMapper.getOrganizationMemberRoles(orgId, userId);
    }

    public List<Role> getWorkspaceMemberRoles(String workspaceId, String userId) {
        return extUserRoleMapper.getWorkspaceMemberRoles(workspaceId, userId);
    }

    public List<Map<String, Object>> getUserRole(String userId) {
        List<Map<String, Object>> list = new ArrayList<>();
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
        List<String> collect = userRoles.stream()
                .map(userRole -> {
                    return userRole.getRoleId();
                })
                .distinct()
                .collect(Collectors.toList());
        for (String s : collect) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("id", s);
            map.put("ids", new ArrayList<>());
            for (UserRole userRole : userRoles) {
                String role = userRole.getRoleId();
                if (StringUtils.equals(role, s)) {
                    List ids = (List) map.get("ids");
                    ids.add(userRole.getSourceId());
                }
            }
            list.add(map);
        }
        return list;
    }
}
