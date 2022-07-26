package com.hummerrisk.dto;

import com.hummerrisk.base.domain.HistoryServerTask;
import com.hummerrisk.base.domain.Task;

import java.util.List;


public class TaskReportDTO extends Task {

    private List<HistoryCloudTaskDTO> historyCloudTaskDTOList;//云账号检测结果table

    private List<HistoryResourceReportDTO> historyCloudResourceReportDTOList;//云账号检测结果details

    private List<HistoryVulnTaskDTO> historyVulnTaskDTOList;//漏洞检测结果table

    private List<HistoryResourceReportDTO> historyVulnResourceReportDTOList;//漏洞检测结果details

    private List<HistoryServerTask> historyServerTaskList;//虚拟机检测结果table

    private List<HistoryImageReportDTO> historyImageReportDTOList;//镜像检测结果details

    private List<HistoryPackageReportDTO> historyPackageReportDTOList;//软件包检测结果details

    public List<HistoryCloudTaskDTO> getHistoryCloudTaskDTOList() {
        return historyCloudTaskDTOList;
    }

    public void setHistoryCloudTaskDTOList(List<HistoryCloudTaskDTO> historyCloudTaskDTOList) {
        this.historyCloudTaskDTOList = historyCloudTaskDTOList;
    }

    public List<HistoryVulnTaskDTO> getHistoryVulnTaskDTOList() {
        return historyVulnTaskDTOList;
    }

    public void setHistoryVulnTaskDTOList(List<HistoryVulnTaskDTO> historyVulnTaskDTOList) {
        this.historyVulnTaskDTOList = historyVulnTaskDTOList;
    }

    public List<HistoryResourceReportDTO> getHistoryCloudResourceReportDTOList() {
        return historyCloudResourceReportDTOList;
    }

    public void setHistoryCloudResourceReportDTOList(List<HistoryResourceReportDTO> historyCloudResourceReportDTOList) {
        this.historyCloudResourceReportDTOList = historyCloudResourceReportDTOList;
    }

    public List<HistoryResourceReportDTO> getHistoryVulnResourceReportDTOList() {
        return historyVulnResourceReportDTOList;
    }

    public void setHistoryVulnResourceReportDTOList(List<HistoryResourceReportDTO> historyVulnResourceReportDTOList) {
        this.historyVulnResourceReportDTOList = historyVulnResourceReportDTOList;
    }

    public List<HistoryServerTask> getHistoryServerTaskList() {
        return historyServerTaskList;
    }

    public void setHistoryServerTaskList(List<HistoryServerTask> historyServerTaskList) {
        this.historyServerTaskList = historyServerTaskList;
    }

    public List<HistoryImageReportDTO> getHistoryImageReportDTOList() {
        return historyImageReportDTOList;
    }

    public void setHistoryImageReportDTOList(List<HistoryImageReportDTO> historyImageReportDTOList) {
        this.historyImageReportDTOList = historyImageReportDTOList;
    }

    public List<HistoryPackageReportDTO> getHistoryPackageReportDTOList() {
        return historyPackageReportDTOList;
    }

    public void setHistoryPackageReportDTOList(List<HistoryPackageReportDTO> historyPackageReportDTOList) {
        this.historyPackageReportDTOList = historyPackageReportDTOList;
    }
}
