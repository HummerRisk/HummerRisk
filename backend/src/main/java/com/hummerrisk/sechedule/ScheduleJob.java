package com.hummerrisk.sechedule;

import com.hummer.quartz.anno.QuartzScheduled;
import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.service.AccountService;
import com.hummerrisk.service.CloudSyncService;
import com.hummerrisk.service.CloudTaskService;
import com.hummerrisk.service.SystemParameterService;
import org.quartz.*;

import javax.annotation.Resource;

public abstract class ScheduleJob implements Job {

    protected String resourceId;

    protected String userId;

    protected String expression;

    @Resource
    private CloudTaskService cloudTaskService;
    @Resource
    private AccountService accountService;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private CloudSyncService cloudSyncService;

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

//    @QuartzScheduled(cron = "${cron.regions.sync}")
//    public void SyncRegions() {
//        accountService.syncRegions();
//    }
//
//    @QuartzScheduled(cron = "${cron.taskSum.sync}")
//    public void SyncTaskSum() {
//        cloudTaskService.syncTaskSum();
//    }
//
//    //每天留一条整体检测记录
//    @QuartzScheduled(cron = "${cron.system.sync}")
//    public void SyncSystem() throws Exception {
//        systemParameterService.updateSystem();
//    }

    //每天自动同步云资源
    @QuartzScheduled(cron = "${cron.system.sync}")
    public void SyncResources() throws Exception {
        cloudSyncService.SyncResources();
    }

}
