package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.Plugin;
import com.hummerrisk.controller.request.plugin.PluginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtPluginMapper {

    List<Plugin> getPluginList(@Param("request") PluginRequest request);

}
