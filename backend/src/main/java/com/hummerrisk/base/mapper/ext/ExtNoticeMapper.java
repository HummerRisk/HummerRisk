package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.MessageOrder;
import com.hummerrisk.base.domain.WebMsg;
import com.hummerrisk.controller.request.webMsg.WebMsgRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ExtNoticeMapper {

    int serverSum(@Param("request") MessageOrder request);

    int imageSum(@Param("request") MessageOrder request);

    int codeSum(@Param("request") MessageOrder request);

    int configSum(@Param("request") MessageOrder request);

    int k8sSum(@Param("request") MessageOrder request);

    int fsSum(@Param("request") MessageOrder request);

}
