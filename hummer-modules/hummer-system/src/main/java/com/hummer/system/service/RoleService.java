package com.hummer.system.service;

import com.hummer.common.core.domain.Role;
import com.hummer.system.mapper.RoleMapper;
import com.hummer.system.mapper.ext.ExtRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    @Autowired
    private ExtRoleMapper extRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    public List<Role> getRoleList(String sign) {
        return extRoleMapper.getRoleList(sign);
    }

    public List<Role> getAllRole() {
        return roleMapper.selectByExample(null);
    }
}
