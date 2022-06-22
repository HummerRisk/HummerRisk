package com.hummerrisk.sechedule;

import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.service.AccountService;
import com.hummerrisk.service.RuleService;
import com.hummerrisk.service.SystemParameterService;
import com.hummerrisk.service.TaskService;
import org.quartz.*;

import javax.annotation.Resource;

public abstract class ScheduleJob implements Job {

    protected String resourceId;

    protected String userId;

    protected String expression;

    @Resource
    private TaskService taskService;
    @Resource
    private AccountService accountService;
    @Resource
    private RuleService ruleService;
    @Resource
    private SystemParameterService systemParameterService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobKey jobKey = context.getTrigger().getJobKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        this.resourceId = jobDataMap.getString("resourceId");
        this.userId = jobDataMap.getString("userId");
        this.expression = jobDataMap.getString("expression");

        LogUtil.info(jobKey.getGroup() + " Running: " + resourceId);
        LogUtil.info("CronExpression: " + expression);
        businessExecute(context);
    }

    abstract void businessExecute(JobExecutionContext context);

    @QuartzScheduled(cron = "${cron.regions.sync}")
    public void SyncRegions() {
        accountService.syncRegions();
    }

    @QuartzScheduled(cron = "${cron.taskSum.sync}")
    public void SyncTaskSum() {
        taskService.syncTaskSum();
    }

    //每天留一条整体检测记录
    @QuartzScheduled(cron = "${cron.history.sync}")
    public void SyncScan() {
        ruleService.syncScanHistory();
    }

    @QuartzScheduled(cron = "${cron.system.sync}")
    public void SyncSystem() throws Exception {
        systemParameterService.updateSystem();
    }

}
