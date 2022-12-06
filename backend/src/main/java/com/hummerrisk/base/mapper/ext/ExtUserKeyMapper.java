package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.UserKey;
import com.hummerrisk.controller.request.user.UserKeyRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserKeyMapper {

    List<UserKey> getUserKeysInfo(@Param("request") UserKeyRequest request);

}
