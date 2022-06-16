package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.User;
import com.hummerrisk.base.domain.UserDetail;
import com.hummerrisk.controller.request.UserRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserMapper {

    List<User> getUserList(@Param("userRequest") UserRequest request);

    int updatePassword(User record);

    String getDefaultLanguage(String paramKey);

    List<User> searchUser(String condition);

    List<UserDetail> queryTypeByIds(List<String> userIds);

}
