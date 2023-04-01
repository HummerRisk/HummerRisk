package com.hummer.system.api;


import com.hummer.common.core.domain.OperationLog;
import com.hummer.common.core.domain.request.log.OperatorLogRequest;
import com.hummer.system.api.model.LoginUser;

import java.util.List;

public interface IOperationLogService {

    List<OperationLog> selectRersourceOperationLog(String resourceId);

    List<OperationLog> selectOperationLog(OperatorLogRequest log);

    void log(LoginUser user, String resourceId, String resourceName, String resourceType, String operation, String message);

    void log(OperationLog operationLog);

    OperationLog createOperationLog(LoginUser user, String resourceId, String resourceName, String resourceType, String operation, String message, String ip);

}
