package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.UserKey;
import com.hummer.common.core.domain.request.user.UserKeyRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserKeyMapper {

    List<UserKey> getUserKeysInfo(@Param("request") UserKeyRequest request);

}
