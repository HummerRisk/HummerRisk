package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.MetricChartDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtNoticeMapper {

    int serverSum(@Param("request") MessageOrder request);

    int imageSum(@Param("request") MessageOrder request);

    int codeSum(@Param("request") MessageOrder request);

    int configSum(@Param("request") MessageOrder request);

    int k8sSum(@Param("request") MessageOrder request);

    int fsSum(@Param("request") MessageOrder request);

    List<ImageResultItem> getTopImageTasksForEmail(@Param("request") MessageOrder request);

    List<CodeResultItem> getTopCodeTasksForEmail(@Param("request") MessageOrder request);

    List<CloudNativeConfigResultItem> getTopConfigTasksForEmail(@Param("request") MessageOrder request);

    List<CloudNativeResultItem> getTopK8sTasksForEmail(@Param("request") MessageOrder request);

    List<FileSystemResultItem> getTopFsTasksForEmail(@Param("request") MessageOrder request);

    List<ServerResultDTO> getTopServerTasksForEmail(@Param("request") MessageOrder request);

    MetricChartDTO metricChartCloud(@Param("request") MessageOrder request);

    MetricChartDTO metricChartServer(@Param("request") MessageOrder request);

    MetricChartDTO metricChartK8s(@Param("request") MessageOrder request);

    MetricChartDTO metricChartConfig(@Param("request") MessageOrder request);

    MetricChartDTO metricChartImage(@Param("request") MessageOrder request);

    MetricChartDTO metricChartCode(@Param("request") MessageOrder request);

    MetricChartDTO metricChartFs(@Param("request") MessageOrder request);

}
