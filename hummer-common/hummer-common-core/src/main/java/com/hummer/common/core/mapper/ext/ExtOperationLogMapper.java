package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.OperationLog;
import com.hummer.common.core.domain.request.log.OperatorLogRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOperationLogMapper {

    public List<OperationLog> selectOperationLog(@Param("request") OperatorLogRequest request);

}
