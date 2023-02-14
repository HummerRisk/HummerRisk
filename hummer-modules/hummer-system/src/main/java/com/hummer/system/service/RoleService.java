package com.hummer.system.service;

import com.hummer.common.core.domain.Role;
import com.hummer.common.mapper.mapper.RoleMapper;
import com.hummer.common.mapper.mapper.ext.ExtRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    @Resource
    private ExtRoleMapper extRoleMapper;
    @Resource
    private RoleMapper roleMapper;

    public List<Role> getRoleList(String sign) {
        return extRoleMapper.getRoleList(sign);
    }

    public List<Role> getAllRole() {
        return roleMapper.selectByExample(null);
    }
}
