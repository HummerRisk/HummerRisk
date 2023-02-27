package com.hummer.common.security.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.PatternMatchUtils;
import com.hummer.common.core.context.SecurityContextHolder;
import com.hummer.common.core.exception.auth.NotLoginException;
import com.hummer.common.core.exception.auth.NotPermissionException;
import com.hummer.common.core.exception.auth.NotRoleException;
import com.hummer.common.core.utils.StringUtils;
import com.hummer.common.security.annotation.Logical;
import com.hummer.common.security.annotation.RequiresLogin;
import com.hummer.common.security.annotation.RequiresPermissions;
import com.hummer.common.security.annotation.RequiresRoles;
import com.hummer.common.security.service.TokenService;
import com.hummer.common.security.utils.SecurityUtils;

/**
 * Token 权限验证，逻辑实现类
 *
 * @author hummer
 */
public class AuthLogic {
}
