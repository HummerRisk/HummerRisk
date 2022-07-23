package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryCloudTask;
import com.hummerrisk.base.domain.HistoryCloudTaskItem;
import com.hummerrisk.base.domain.HistoryServerTask;
import com.hummerrisk.base.domain.HistoryVulnTaskItem;

import java.util.List;

/**
 * @author harris
 */
public class HistoryCloudTaskReportDTO extends HistoryCloudTask {

    private List<HistoryCloudTaskItem> historyCloudTaskItemList;//云账号检测结果table

    private List<HistoryResourceReportDTO> historyCloudResourceReportDTOList;//云账号检测结果details

    private List<HistoryVulnTaskItem> historyVulnTaskItemList;//漏洞检测结果table

    private List<HistoryResourceReportDTO> historyVulnResourceReportDTOList;//漏洞检测结果details

    private List<HistoryServerTask> historyServerTaskList;//虚拟机检测结果table



}
