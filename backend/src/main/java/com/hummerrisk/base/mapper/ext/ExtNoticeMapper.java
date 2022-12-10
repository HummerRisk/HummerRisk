package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.MessageOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExtNoticeMapper {

    int serverSum(@Param("request") MessageOrder request);

    int imageSum(@Param("request") MessageOrder request);

    int codeSum(@Param("request") MessageOrder request);

    int configSum(@Param("request") MessageOrder request);

    int k8sSum(@Param("request") MessageOrder request);

    int fsSum(@Param("request") MessageOrder request);

}
