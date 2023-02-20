package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.OperationLog;
import com.hummer.common.mapper.domain.request.log.OperatorLogRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOperationLogMapper {

    public List<OperationLog> selectOperationLog(@Param("request") OperatorLogRequest request);

}
