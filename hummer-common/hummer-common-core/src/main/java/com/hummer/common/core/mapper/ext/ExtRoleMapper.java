package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRoleMapper {

    List<Role> getRoleList(@Param("sign") String sign);
}
