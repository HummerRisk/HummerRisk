package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.UserKey;
import com.hummer.common.mapper.domain.request.user.UserKeyRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtUserKeyMapper {

    List<UserKey> getUserKeysInfo(@Param("request") UserKeyRequest request);

}
