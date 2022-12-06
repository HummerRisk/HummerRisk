package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.OperationLog;
import com.hummerrisk.controller.request.log.OperatorLogRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtOperationLogMapper {

    public List<OperationLog> selectOperationLog(@Param("request") OperatorLogRequest request);

}
