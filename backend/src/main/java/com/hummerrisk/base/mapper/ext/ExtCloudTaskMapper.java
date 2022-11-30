package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CloudTask;
import com.hummerrisk.base.domain.CloudTaskExample;
import com.hummerrisk.base.domain.MessageOrder;
import com.hummerrisk.controller.request.cloudTask.CloudTaskSearchRequest;
import com.hummerrisk.controller.request.cloudTask.ManualRequest;
import com.hummerrisk.dto.CloudTaskDTO;
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
}
