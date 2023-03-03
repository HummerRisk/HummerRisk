package com.hummer.auth.service;

import com.hummer.auth.mapper.UserMapper;
import com.hummer.common.core.domain.User;
import com.hummer.common.core.domain.UserExample;
import com.hummer.system.api.model.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Resource
    private UserMapper userMapper;

    public LoginUser getLoginUserByName(String userName) throws Exception {
        LoginUser loginUser = new LoginUser();
        UserExample example = new UserExample();
        example.createCriteria().andNameEqualTo(userName);
        List<User> users = userMapper.selectByExample(example);
        com.hummer.system.api.domain.User user = new com.hummer.system.api.domain.User();
        BeanUtils.copyProperties(user, users.get(0));
        loginUser.setUser(user);
        return loginUser;
    }

}
