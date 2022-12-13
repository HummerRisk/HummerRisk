package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.*;
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

    List<ServerResult> getTopServerTasksForEmail(@Param("request") MessageOrder request);

}
