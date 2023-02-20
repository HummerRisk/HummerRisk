package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.dto.CloudTaskDTO;
import com.hummer.common.mapper.domain.CloudTask;
import com.hummer.common.mapper.domain.CloudTaskExample;
import com.hummer.common.mapper.domain.MessageOrder;
import com.hummer.common.mapper.domain.request.cloudTask.CloudTaskSearchRequest;
import com.hummer.common.mapper.domain.request.cloudTask.ManualRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudTaskMapper {

    CloudTaskDTO getTaskDetail(@Param("taskId") String taskId);

    List<CloudTask> searchTask(CloudTaskSearchRequest request);

    CloudTaskDTO getTaskExtendInfo(String taskId);

    int getResourceSum(String taskId);

    int getReturnSum(String taskId);

    List<CloudTask> getTopTasksForEmail(MessageOrder messageOrder);

    int getReturnSumForEmail(MessageOrder messageOrder);

    int getResourcesSumForEmail(MessageOrder messageOrder);

    List<CloudTaskDTO> selectByExample(CloudTaskExample example);

    List<CloudTask> selectByHummerId(String hummerId,String regionId);

    List<CloudTask> selectManualTasks(@Param("request") ManualRequest request);

    List<CloudTask> selectManualVulnTasks(@Param("request") ManualRequest request);

    List<CloudTask> selectOssManualTasks(@Param("request") ManualRequest request);
}
