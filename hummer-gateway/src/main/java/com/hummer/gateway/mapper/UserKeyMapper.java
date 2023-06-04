package com.hummer.gateway.mapper;

import com.hummer.common.core.domain.UserKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserKeyMapper {

    UserKey getUserKey(String accessKey);
}
