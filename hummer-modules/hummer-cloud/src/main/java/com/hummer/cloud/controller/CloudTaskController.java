package com.hummer.cloud.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hummer.cloud.service.CloudTaskService;
import com.hummer.cloud.service.OrderService;
import com.hummer.cloud.service.ResourceCreateService;
import com.hummer.common.core.domain.CloudTask;
import com.hummer.common.core.domain.request.cloudTask.ManualRequest;
import com.hummer.common.core.dto.CloudTaskCopyDTO;
import com.hummer.common.core.dto.CloudTaskDTO;
import com.hummer.common.core.dto.CloudTaskItemLogDTO;
import com.hummer.common.core.dto.QuartzTaskDTO;
import com.hummer.common.core.handler.annotation.I18n;
import com.hummer.common.core.utils.PageUtils;
import com.hummer.common.core.utils.Pager;
import com.hummer.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RestController
@RequestMapping(value = "cloud/task")
public class CloudTaskController {
    @Autowired
    private CloudTaskService cloudTaskService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ResourceCreateService resourceCreateService;

    @GetMapping("run")
    public String cloudTask() throws Exception {
        resourceCreateService.cloudTasksJobHandler();
        return "success";
    }

    @GetMapping("sync/resource")
    public String syncResourceTasks() throws Exception{
        resourceCreateService.syncResourceTasksJobHandler();
        return "success";
    }

    @GetMapping("sync/oss")
    public String  ossSyncTasks() throws Exception {
        resourceCreateService.ossTasksJobHandler();
        return "success";
    }

    @I18n
    @GetMapping(value = "detail/{taskId}")
    public CloudTaskDTO getTaskDetail(@PathVariable String taskId) {
        return orderService.getTaskDetail(taskId);
    }

    @I18n
    @GetMapping(value = "copy/{taskId}")
    public CloudTaskCopyDTO copy(@PathVariable String taskId) {
        return orderService.copy(taskId);
    }

    @I18n
    @GetMapping(value = "log/taskId/{taskId}")
    public List<CloudTaskItemLogDTO> getTaskItemLogByTask(@PathVariable String taskId) {
        return orderService.getTaskItemLogByTaskId(taskId);
    }

    @GetMapping(value = "extendinfo/{taskId}")
    public CloudTaskDTO getTaskExtendInfo(@PathVariable String taskId) {
        return orderService.getTaskExtendInfo(taskId);
    }

    @PostMapping(value = "retry/{taskId}")
    public void retryTask(@PathVariable String taskId) throws Exception {
        orderService.retry(taskId, tokenService.getLoginUser());
    }

    @I18n
    @PostMapping("manual/list/{goPage}/{pageSize}")
    public Pager<List<CloudTask>> getManualTasks(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ManualRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setType("manual");
        return PageUtils.setPageInfo(page, cloudTaskService.selectManualTasks(request));
    }

    @GetMapping("manual/more/{taskId}")
    public boolean morelTask(@PathVariable String taskId) {
        return cloudTaskService.morelTask(taskId);
    }

    @I18n
    @PostMapping("manual/create")
    public CloudTask saveManualTask(@RequestBody QuartzTaskDTO quartzTaskDTO) {
        quartzTaskDTO.setType("manual");
        return cloudTaskService.saveManualTask(quartzTaskDTO, null, tokenService.getLoginUser());
    }

    @PostMapping("manual/delete")
    public void deleteManualTask(@RequestBody String quartzTaskId) {
        cloudTaskService.deleteManualTask(quartzTaskId, tokenService.getLoginUser());
    }

    @PostMapping(value = "manual/dryRun")
    public boolean dryRun(@RequestBody QuartzTaskDTO quartzTaskDTO) {
        quartzTaskDTO.setType("manual");
        return cloudTaskService.dryRun(quartzTaskDTO);
    }


}
