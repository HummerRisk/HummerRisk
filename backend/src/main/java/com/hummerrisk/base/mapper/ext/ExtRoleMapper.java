package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtRoleMapper {

    List<Role> getRoleList(@Param("sign") String sign);
}
