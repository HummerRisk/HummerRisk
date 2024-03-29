package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.User;
import com.hummer.common.core.domain.UserDetail;
import com.hummer.common.core.domain.request.member.UserRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserMapper {

    List<User> getUserList(@Param("request") UserRequest request);

    int updatePassword(User record);

    String getDefaultLanguage(String paramKey);

    List<User> searchUser(String condition);

    List<UserDetail> queryTypeByIds(List<String> userIds);

}
