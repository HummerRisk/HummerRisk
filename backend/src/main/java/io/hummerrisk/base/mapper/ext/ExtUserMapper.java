package io.hummerrisk.base.mapper.ext;

import io.hummerrisk.base.domain.User;
import io.hummerrisk.base.domain.UserDetail;
import io.hummerrisk.controller.request.UserRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserMapper {

    List<User> getUserList(@Param("userRequest") UserRequest request);

    int updatePassword(User record);

    String getDefaultLanguage(String paramKey);

    List<User> searchUser(String condition);

    List<UserDetail> queryTypeByIds(List<String> userIds);

}
