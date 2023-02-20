package com.hummer.common.mapper.dto;


import com.hummer.common.mapper.domain.HistoryServerResult;
import com.hummer.common.mapper.domain.Task;

import java.util.List;


public class TaskReportDTO extends Task {

    private List<HistoryCloudTaskDTO> historyCloudTaskDTOList;//云账号检测结果table

    private List<HistoryResourceReportDTO> historyCloudResourceReportDTOList;//云账号检测结果details

    private List<HistoryVulnTaskDTO> historyVulnTaskDTOList;//漏洞检测结果details

    private List<HistoryResourceReportDTO> historyVulnResourceReportDTOList;//漏洞检测结果details

    private List<HistoryServerResult> historyServerResultList;//主机检测结果details

    private List<HistoryImageReportDTO> historyImageReportDTOList;//镜像检测结果details

    private List<HistoryCodeReportDTO> historyCodeReportDTOList;//源码检测结果details

    private List<HistoryConfigReportDTO> historyConfigReportDTOList;//部署检测结果details

    private List<HistoryK8sReportDTO> historyK8sReportDTOList;//K8s检测结果details

    private List<HistoryFsReportDTO> historyFsReportDTOList;//文件检测结果details

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

    public List<HistoryServerResult> getHistoryServerResultList() {
        return historyServerResultList;
    }

    public void setHistoryServerResultList(List<HistoryServerResult> historyServerResultList) {
        this.historyServerResultList = historyServerResultList;
    }

    public List<HistoryCodeReportDTO> getHistoryCodeReportDTOList() {
        return historyCodeReportDTOList;
    }

    public void setHistoryCodeReportDTOList(List<HistoryCodeReportDTO> historyCodeReportDTOList) {
        this.historyCodeReportDTOList = historyCodeReportDTOList;
    }

    public List<HistoryImageReportDTO> getHistoryImageReportDTOList() {
        return historyImageReportDTOList;
    }

    public void setHistoryImageReportDTOList(List<HistoryImageReportDTO> historyImageReportDTOList) {
        this.historyImageReportDTOList = historyImageReportDTOList;
    }

    public List<HistoryFsReportDTO> getHistoryFsReportDTOList() {
        return historyFsReportDTOList;
    }

    public void setHistoryFsReportDTOList(List<HistoryFsReportDTO> historyFsReportDTOList) {
        this.historyFsReportDTOList = historyFsReportDTOList;
    }

    public List<HistoryConfigReportDTO> getHistoryConfigReportDTOList() {
        return historyConfigReportDTOList;
    }

    public void setHistoryConfigReportDTOList(List<HistoryConfigReportDTO> historyConfigReportDTOList) {
        this.historyConfigReportDTOList = historyConfigReportDTOList;
    }

    public List<HistoryK8sReportDTO> getHistoryK8sReportDTOList() {
        return historyK8sReportDTOList;
    }

    public void setHistoryK8sReportDTOList(List<HistoryK8sReportDTO> historyK8sReportDTOList) {
        this.historyK8sReportDTOList = historyK8sReportDTOList;
    }
}
