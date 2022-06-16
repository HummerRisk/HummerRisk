package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Task;
import com.hummerrisk.controller.request.task.TaskSearchRequest;
import com.hummerrisk.base.domain.MessageOrder;
import com.hummerrisk.dto.TaskDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtTaskMapper {

    TaskDTO getTaskDetail(@Param("taskId") String taskId);

    List<Task> searchTask(TaskSearchRequest request);

    TaskDTO getTaskExtendInfo(String taskId);

    int getResourceSum(String taskId);

    int getReturnSum(String taskId);

    List<Task> getTopTasksForEmail(MessageOrder messageOrder);

    int getReturnSumForEmail(MessageOrder messageOrder);

    int getResourcesSumForEmail(MessageOrder messageOrder);
}
