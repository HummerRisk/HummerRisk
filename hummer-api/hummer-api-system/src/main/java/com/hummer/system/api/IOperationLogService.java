package com.hummer.system.api;


import com.hummer.common.core.domain.OperationLog;
import com.hummer.common.core.domain.request.log.OperatorLogRequest;
import com.hummer.common.core.dto.UserDTO;

import java.util.List;

public interface IOperationLogService {

    List<OperationLog> selectRersourceOperationLog(String resourceId);

    List<OperationLog> selectOperationLog(OperatorLogRequest log);

    void log(UserDTO user, String resourceId, String resourceName, String resourceType, String operation, String message);

    void log(OperationLog operationLog);

    OperationLog createOperationLog(UserDTO user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip);

}
