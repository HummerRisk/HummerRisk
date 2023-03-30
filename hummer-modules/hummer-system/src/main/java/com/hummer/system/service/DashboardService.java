package com.hummer.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.ParamConstants;
import com.hummer.common.core.constant.TaskConstants;
import com.hummer.common.core.constant.TaskEnum;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.chart.ChartData;
import com.hummer.common.core.domain.request.dashboard.AnslysisVo;
import com.hummer.common.core.domain.request.dashboard.DashboardTarget;
import com.hummer.common.core.domain.request.dashboard.HistoryScanVo;
import com.hummer.common.core.domain.request.dashboard.TaskCalendarVo;
import com.hummer.common.core.dto.ChartDTO;
import com.hummer.common.core.dto.HistoryScanDTO;
import com.hummer.common.core.dto.TopInfoDTO;
import com.hummer.common.core.dto.TopScanDTO;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.system.mapper.SystemParameterMapper;
import com.hummer.system.mapper.ext.ExtDashboardMapper;
import com.hummer.system.mapper.ext.ExtVulnMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.alibaba.fastjson.JSON.parseArray;

@Service
public class DashboardService {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private ExtVulnMapper extVulnMapper;
    @Autowired
    private ExtDashboardMapper extDashboardMapper;
    @Autowired
    private SystemParameterMapper systemParameterMapper;
    @DubboReference
    private ICloudProviderService cloudProviderService;
    @DubboReference
    private IK8sProviderService k8sProviderService;


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
            case "regionsList":
                return extVulnMapper.regionsList(params);
            case "countList":
                return extVulnMapper.countList(params);
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

    public List<HistoryScanDTO> history(Map<String, Object> params) {
        List<HistoryScanDTO> historyList = extVulnMapper.history(params);
        for (HistoryScanDTO scanHistory : historyList) {
            scanHistory.setOutput(toJSONString2(scanHistory.getOutput() != null ? scanHistory.getOutput() : "[]"));
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

    public List<TopScanDTO> topScanInfo() {
        List<TopScanDTO> list = new ArrayList<>();
        list.add(extDashboardMapper.topScanInfo("APPROVED"));
        list.add(extDashboardMapper.topScanInfo("FINISHED"));
        list.add(extDashboardMapper.topScanInfo("ERROR"));
        list.add(extDashboardMapper.topScanInfo("WARNING"));
        return list;
    }

    public ChartDTO imageChart(Map<String, Object> params) {
        ChartDTO imageChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.imageChartX(params);
        List<Integer> yAxis = extDashboardMapper.imageChartY(params);
        imageChartDTO.setXAxis(xAxis);
        imageChartDTO.setYAxis(yAxis);
        return imageChartDTO;
    }

    public ChartDTO codeChart(Map<String, Object> params) {
        ChartDTO codeChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.codeChartX(params);
        List<Integer> yAxis = extDashboardMapper.codeChartY(params);
        codeChartDTO.setXAxis(xAxis);
        codeChartDTO.setYAxis(yAxis);
        return codeChartDTO;
    }

    public ChartDTO cloudNativeChart(Map<String, Object> params) {
        ChartDTO cloudNativeChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.cloudNativeChartX(params);
        List<Integer> yAxis = extDashboardMapper.cloudNativeChartY(params);
        cloudNativeChartDTO.setXAxis(xAxis);
        cloudNativeChartDTO.setYAxis(yAxis);
        return cloudNativeChartDTO;
    }

    public ChartDTO configChart(Map<String, Object> params) {
        ChartDTO cloudNativeChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.configChartX(params);
        List<Integer> yAxis = extDashboardMapper.configChartY(params);
        cloudNativeChartDTO.setXAxis(xAxis);
        cloudNativeChartDTO.setYAxis(yAxis);
        return cloudNativeChartDTO;
    }

    public ChartDTO fsChart(Map<String, Object> params) {
        ChartDTO fsChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.fsChartX(params);
        List<Integer> yAxis = extDashboardMapper.fsChartY(params);
        fsChartDTO.setXAxis(xAxis);
        fsChartDTO.setYAxis(yAxis);
        return fsChartDTO;
    }

    public List<TaskCalendarVo> taskCalendar() {
        return extDashboardMapper.taskCalendar();
    }

    public Integer score() {
        int score = 100, sum = 0, count = 0;//计数器

        CloudTaskExample cloudTaskExample = new CloudTaskExample();
        cloudTaskExample.createCriteria().andStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<CloudTask> cloudTasks = cloudProviderService.selectCloudTaskList(cloudTaskExample);
        for (CloudTask cloudTask : cloudTasks) {
            sum = sum + historyService.calculateScore(cloudTask.getId(), cloudTask, TaskEnum.cloudAccount.getType());
        }

        CloudNativeResultExample cloudNativeResultExample = new CloudNativeResultExample();
        cloudNativeResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<CloudNativeResult> cloudNativeResults = k8sProviderService.cloudNativeResults(cloudNativeResultExample);
        for (CloudNativeResult cloudNativeResult : cloudNativeResults) {
            sum = sum + historyService.calculateScore(cloudNativeResult.getId(), cloudNativeResult, TaskEnum.k8sAccount.getType());
        }

        CloudNativeConfigResultExample cloudNativeConfigResultExample = new CloudNativeConfigResultExample();
        cloudNativeConfigResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<CloudNativeConfigResult> cloudNativeConfigResults = k8sProviderService.cloudNativeConfigResults(cloudNativeConfigResultExample);
        for (CloudNativeConfigResult cloudNativeConfigResult : cloudNativeConfigResults) {
            sum = sum + historyService.calculateScore(cloudNativeConfigResult.getId(), cloudNativeConfigResult, TaskEnum.configAccount.getType());
        }

        ServerResultExample serverResultExample = new ServerResultExample();
        serverResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<ServerResult> serverResults = k8sProviderService.serverResults(serverResultExample);
        for (ServerResult serverResult : serverResults) {
            sum = sum + historyService.calculateScore(serverResult.getId(), serverResult, TaskEnum.serverAccount.getType());
        }

        ImageResultExample imageResultExample = new ImageResultExample();
        imageResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<ImageResult> imageResults = k8sProviderService.imageResults(imageResultExample);
        for (ImageResult imageResult : imageResults) {
            sum = sum + historyService.calculateScore(imageResult.getId(), imageResult, TaskEnum.imageAccount.getType());
        }

        CodeResultExample codeResultExample = new CodeResultExample();
        codeResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<CodeResult> codeResults = k8sProviderService.codeResults(codeResultExample);
        for (CodeResult codeResult : codeResults) {
            sum = sum + historyService.calculateScore(codeResult.getId(), codeResult, TaskEnum.codeAccount.getType());
        }

        FileSystemResultExample fileSystemResultExample = new FileSystemResultExample();
        fileSystemResultExample.createCriteria().andResultStatusEqualTo(TaskConstants.TASK_STATUS.FINISHED.toString());
        List<FileSystemResult> fileSystemResults = k8sProviderService.fileSystemResults(fileSystemResultExample);
        for (FileSystemResult fileSystemResult : fileSystemResults) {
            sum = sum + historyService.calculateScore(fileSystemResult.getId(), fileSystemResult, TaskEnum.fsAccount.getType());
        }

        count = cloudTasks.size() + cloudNativeResults.size() + cloudNativeConfigResults.size() + serverResults.size() + imageResults.size() + codeResults.size() + fileSystemResults.size();

        if (count != 0) score = Math.round(sum / count);

        return score;
    }

    public void saveAnalysis(AnslysisVo anslysisVo) {
        ParamConstants.ANALYSIS[] values = ParamConstants.ANALYSIS.values();
        for (ParamConstants.ANALYSIS value : values) {
            SystemParameter systemParameter = new SystemParameter();
            systemParameter.setParamKey(value.getKey());
            systemParameter.setSort(value.getValue());
            systemParameter.setType("system");
            if (getValue(value.getKey()) != null) {
                if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.COLOR.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getColor());
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.CYCLE.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getCycle().toString());
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.IDS.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getIds().toString().replace("[", "").replace("]", ""));
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.TYPES.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getTypes().toString().replace("[", "").replace("]", ""));
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.USERS.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getUsers().toString().replace("[", "").replace("]", ""));
                }
                systemParameterMapper.updateByPrimaryKeySelective(systemParameter);
            } else {
                if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.COLOR.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getColor());
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.CYCLE.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getCycle().toString());
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.IDS.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getIds().toString().replace("[", "").replace("]", ""));
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.TYPES.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getTypes().toString().replace("[", "").replace("]", ""));
                } else if (StringUtils.equalsIgnoreCase(value.getKey(), ParamConstants.ANALYSIS.USERS.getKey())) {
                    systemParameter.setParamValue(anslysisVo.getUsers().toString().replace("[", "").replace("]", ""));
                }
                systemParameterMapper.insertSelective(systemParameter);
            }
        }
    }

    public List<SystemParameter> getParamList(String type) {
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyLike(type + "%");
        return systemParameterMapper.selectByExample(example);
    }

    private String getValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter != null) {
            return systemParameter.getParamValue();
        }
        return null;
    }

    public AnslysisVo queryAnalysis() {
        AnslysisVo anslysisVo = new AnslysisVo();
        anslysisVo.setColor(getValue(ParamConstants.ANALYSIS.COLOR.getKey()) != null ? getValue(ParamConstants.ANALYSIS.COLOR.getKey()) : ParamConstants.ANALYSIS.color);
        anslysisVo.setCycle(getValue(ParamConstants.ANALYSIS.CYCLE.getKey()) != null ? Integer.valueOf(getValue(ParamConstants.ANALYSIS.CYCLE.getKey())) : ParamConstants.ANALYSIS.cycle);
        List<Boolean> list = new ArrayList<Boolean>();
        if (getValue(ParamConstants.ANALYSIS.IDS.getKey()) != null) {
            String[] strs = getValue(ParamConstants.ANALYSIS.IDS.getKey()).split(",");
            for (String s : strs) {
                list.add(Boolean.parseBoolean(s.trim()));
            }
        }
        anslysisVo.setIds(getValue(ParamConstants.ANALYSIS.IDS.getKey()) != null ? list : ParamConstants.ANALYSIS.ids);
        anslysisVo.setTypes(getValue(ParamConstants.ANALYSIS.TYPES.getKey()) != null ? Arrays.asList(getValue(ParamConstants.ANALYSIS.TYPES.getKey()).replace(" ", "").split(",")) : ParamConstants.ANALYSIS.types);
        anslysisVo.setUsers(getValue(ParamConstants.ANALYSIS.USERS.getKey()) != null ? Arrays.asList(getValue(ParamConstants.ANALYSIS.USERS.getKey()).replace(" ", "").split(",")) : ParamConstants.ANALYSIS.users);
        return anslysisVo;
    }

    public ChartDTO analysisChart() {
        AnslysisVo anslysisVo = queryAnalysis();
        ChartDTO analysisChartDTO = new ChartDTO();
        List<String> xAxis = extDashboardMapper.analysisChartX(anslysisVo);
        List<Integer> yAxis = extDashboardMapper.analysisChartY(anslysisVo);
        analysisChartDTO.setXAxis(xAxis);
        analysisChartDTO.setYAxis(yAxis);
        return analysisChartDTO;
    }

    public List<HistoryScanVo> historyScanVo(HistoryScanVo historyScanVo) {
        return extDashboardMapper.historyScanVo(historyScanVo);
    }

}

