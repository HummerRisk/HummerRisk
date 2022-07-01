package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hummerrisk.base.mapper.ext.ExtDashboardMapper;
import com.hummerrisk.base.mapper.ext.ExtVulnMapper;
import com.hummerrisk.commons.utils.ChartData;
import com.hummerrisk.commons.utils.DashboardTarget;
import com.hummerrisk.dto.ImageChartDTO;
import com.hummerrisk.dto.PackageChartDTO;
import com.hummerrisk.dto.CloudScanHistoryDTO;
import com.hummerrisk.dto.TopInfoDTO;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseArray;

@Service
public class DashboardService {

    @Resource
    private ExtVulnMapper extVulnMapper;
    @Resource
    private ExtDashboardMapper extDashboardMapper;

    public List<ChartData> vulnDistribution(Map<String, Object> params) {

        final String group = MapUtils.getString(params, "group");
        switch (group) {
            case "overall":
                return extVulnMapper.overall(params);
            case "ruleGroup":
                return extVulnMapper.ruleGroup(params);
            case "report":
                return extVulnMapper.report(params);
            case "ruleList":
                return extVulnMapper.ruleList(params);
            case "accountList":
                return extVulnMapper.accountList(params);
            case "vulnList":
                return extVulnMapper.vulnList(params);
            case "regionsList":
                return extVulnMapper.regionsList(params);
            default:
                return new LinkedList<>();
        }
    }

    public List<Map<String, Object>> severityList(Map<String, Object> params) {
        return extVulnMapper.severityList(params);
    }

    public List<Map<String, Object>> totalPolicy(Map<String, Object> params) {
        return extVulnMapper.totalPolicy(params);
    }

    public List<DashboardTarget> target(Map<String, Object> params) {
        return extVulnMapper.target(params);
    }

    public List<CloudScanHistoryDTO> history(Map<String, Object> params) {
        List<CloudScanHistoryDTO> historyList = extVulnMapper.history(params);
        for (CloudScanHistoryDTO scanHistory : historyList) {
            scanHistory.setOutput(toJSONString2(scanHistory.getOutput()!=null?scanHistory.getOutput():"[]"));
        }
        return historyList;
    }

    public List<CloudScanHistoryDTO> vulnHistory(Map<String, Object> params) {
        List<CloudScanHistoryDTO> historyList = extVulnMapper.vulnHistory(params);
        for (CloudScanHistoryDTO scanHistory : historyList) {
            scanHistory.setOutput(toJSONString2(scanHistory.getOutput()!=null?scanHistory.getOutput():"[]"));
        }
        return historyList;
    }

    public String toJSONString2(String jsonString) {
        String res = JSON.parse(jsonString).toString();
        JSONArray jsonArray = parseArray(res);
        return JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    public TopInfoDTO topInfo(Map<String, Object> params) {
        return extDashboardMapper.topInfo(params);
    }

    public PackageChartDTO packageChart(Map<String, Object> params) {
        PackageChartDTO packageChartDTO = new PackageChartDTO();
        List<String> xAxis = extDashboardMapper.packageChartX(params);
        List<Integer> yAxis = extDashboardMapper.packageChartY(params);
        packageChartDTO.setxAxis(xAxis);
        packageChartDTO.setyAxis(yAxis);
        return packageChartDTO;
    }

    public ImageChartDTO imageChart(Map<String, Object> params) {
        ImageChartDTO imageChartDTO = new ImageChartDTO();
        List<String> xAxis = extDashboardMapper.imageChartX(params);
        List<Integer> yAxis = extDashboardMapper.imageChartY(params);
        imageChartDTO.setxAxis(xAxis);
        imageChartDTO.setyAxis(yAxis);
        return imageChartDTO;
    }

}

